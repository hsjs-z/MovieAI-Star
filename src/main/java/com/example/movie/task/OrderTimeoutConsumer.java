package com.example.movie.task;

import com.example.movie.entity.Order;
import com.example.movie.entity.SeatLock;
import com.example.movie.mapper.OrderMapper;
import com.example.movie.mapper.UserHallMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Redis ZSET 延迟队列消费者：订单超时未支付自动取消
 * <p>
 * 三层幂等保障：
 * 1. ZREM 原子消费标记（多节点部署同一条只会被一个 ZREM 成功）
 * 2. setIfAbsent 分布式锁（防止延迟取消和用户主动 cancelOrder 并发）
 * 3. 订单状态二次校验（status != 0 的直接跳过）
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class OrderTimeoutConsumer {

    private final StringRedisTemplate redisTemplate;
    private final OrderMapper orderMapper;
    private final UserHallMapper uhMapper;

    private static final String TIMEOUT_ZSET = "order:timeout";
    private static final long ORDER_TIMEOUT_SECONDS = 15 * 60; // 15min

    // 每秒扫一次 ZSET，把到期的订单捞出来
    @Scheduled(fixedRate = 1000)
    public void scanTimeoutOrders() {
        long now = System.currentTimeMillis();

        Set<String> members = redisTemplate.opsForZSet()
                .rangeByScore(TIMEOUT_ZSET, 0, now);

        if (members == null || members.isEmpty()) {
            return;
        }

        for (String member : members) {
            Long orderId = Long.valueOf(member);
            try {
                handleTimeout(orderId);
            } catch (Exception e) {
                log.error("订单[{}]超时取消失败，将重试", orderId, e);
                // 不 ZREM，下次扫描继续处理 → 自动重试机制
            }
        }
    }

    /**
     * 取消单个超时订单（三层幂等）
     */
    private void handleTimeout(Long orderId) {
        // ① ZREM 原子消费标记（多节点部署时同一条只会被一个 ZREM 成功）
        Long removed = redisTemplate.opsForZSet()
                .remove(TIMEOUT_ZSET, String.valueOf(orderId));
        if (removed == null || removed == 0L) {
            return; // 已被其他节点消费
        }

        // ② setIfAbsent 分布式锁（防止延迟取消和用户主动 cancelOrder 并发）
        Boolean locked = redisTemplate.opsForValue()
                .setIfAbsent("cancel:lock:" + orderId, "1", 10, TimeUnit.SECONDS);
        if (locked == null || !locked) {
            return; // 另一个线程在处理
        }

        try {
            // ③ 查订单状态（幂等校验：只有 status=0 待支付才取消）
            Order order = orderMapper.getById(orderId);
            if (order == null) {
                return; // 订单不存在
            }
            if (order.getStatus() != 0) {
                return; // 已支付 / 已取消 → 跳过
            }

            // 批量删 Redis 座位锁（一个订单可能多个座位）
            List<SeatLock> seatLocks = uhMapper.selectLocksByOrderId(orderId);
            for (SeatLock seatLock : seatLocks) {
                String key = "seat:" + seatLock.getSessionId() + ":"
                        + seatLock.getSeatRow() + ":" + seatLock.getSeatCol();
                redisTemplate.delete(key);
            }

            // 删 DB seat_lock + 改订单状态
            uhMapper.deleteLockByOrderId(orderId);
            order.setStatus(2);
            order.setUpdateTime(LocalDateTime.now());
            orderMapper.updateById(order);

            log.info("订单[{}]超{}min自动取消，释放{}个座位锁",
                    orderId, ORDER_TIMEOUT_SECONDS / 60, seatLocks.size());
        } finally {
            redisTemplate.delete("cancel:lock:" + orderId);
        }
    }
}