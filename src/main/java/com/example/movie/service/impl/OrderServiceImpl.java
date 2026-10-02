package com.example.movie.service.impl;

import com.example.movie.bedto.OrderCreateDTO;
import com.example.movie.bedto.OrderItem;
import com.example.movie.bedto.SeatDTO;
import com.example.movie.common.result.PageResult;
import com.example.movie.dto.*;
import com.example.movie.entity.Order;
import com.example.movie.entity.SeatLock;
import com.example.movie.entity.Session;
import com.example.movie.mapper.OrderItemMapper;
import com.example.movie.mapper.OrderMapper;
import com.example.movie.mapper.SessionMapper;
import com.example.movie.mapper.UserHallMapper;
import com.example.movie.service.OrderService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 作者: 枷
 * 时间:2026/7/30 上午11:07
 * 描述:。。。
 */
@Service
@Slf4j
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private SessionMapper sessionMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private UserHallMapper uhMapper;
    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 获取订单分页
     * @param dto
     * @return
     */
    public PageResult getOrderPage(OrderPageQueryDTO dto) {
        PageHelper.startPage(dto.getPage(), dto.getPageSize());
        Page<OrderDTO> page = orderMapper.getPageOrder(dto);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 取消订单（Admin 端：不需要校验归属）
     */
    public void updateOrderStatus(Long id) {
        Order order = orderMapper.getById(id);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (order.getStatus() != 0) {
            throw new RuntimeException("只有待支付的订单能取消");
        }
        order.setStatus(2);
        order.setUpdateTime(LocalDateTime.now());
        orderMapper.updateById(order);
    }

    /**
     * 取消订单（用户端：校验归属 + 校验状态 + 删 seat_lock + 改订单状态 + 兜底删 Redis）
     */
    @Transactional
    public void cancelOrder(Long id, Long currentUserId) {
        Order order = orderMapper.getById(id);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        if (!order.getUserId().equals(currentUserId)) {
            throw new RuntimeException("只能取消自己的订单");
        }

        if (order.getStatus() != 0) {
            throw new RuntimeException("只有待支付的订单能取消");
        }

        // 先从延迟队列移除（主动取消了就不用再等超时了）
        redisTemplate.opsForZSet().remove("order:timeout", String.valueOf(id));

        // 批量删除 Redis 座位锁（一个订单可能对应多个 seat_lock）
        List<SeatLock> seatLocks = uhMapper.selectLocksByOrderId(id);
        for (SeatLock seatLock : seatLocks) {
            String key = "seat:" + seatLock.getSessionId() + ":"
                    + seatLock.getSeatRow() + ":" + seatLock.getSeatCol();
            redisTemplate.delete(key);
        }

        uhMapper.deleteLockByOrderId(id);

        order.setStatus(2);
        order.setUpdateTime(LocalDateTime.now());
        orderMapper.updateById(order);

        log.info("用户[{}]取消了订单[{}]，释放座位锁", currentUserId, id);
    }

    /**
     * 支付订单
     * @param id
     */
    @Transactional
    public void payOrder(Long id) {
        Order order = orderMapper.getById(id);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (order.getStatus() != 0) {
            throw new RuntimeException("只有待支付订单才能支付");
        }
        // 支付成功 → 从延迟队列移除
        redisTemplate.opsForZSet().remove("order:timeout", String.valueOf(id));
        // 更新状态 + 支付时间
        order.setStatus(1);
        order.setPayTime(LocalDateTime.now());
        orderMapper.updateById(order);
    }

    /**
     * 退款处理
     * @param id
     */
    @Transactional
    public void beOrder(Long id) {
        Order order = orderMapper.getById(id);
        if (order.getStatus() != 1){
            throw new RuntimeException("只有已支付订单才能退款");
        }
        // 更新状态 + 支付时间
        order.setStatus(3);
        order.setRefundTime(LocalDateTime.now());
        orderMapper.updateById(order);
    }

    /**
     * 获取订单详情
     * @param id
     * @return
     */
    public OrderDetailDTO getOrderDetail(Long id) {
        OrderDetailDTO order = orderMapper.selById(id);
        if (order == null){
            throw new RuntimeException("订单不存在");
        }
        List<TicketDTO> tickets = orderMapper.selTicById(id);
        order.setTickets(tickets);
        log.info("订单详情{}", order);
        log.info("影票信息{}", tickets);
        return order;
    }


     //创建订单
    @Transactional
    public Map<String, Object> createOrder(OrderCreateDTO dto, Long userId) {
        Session session = sessionMapper.getById(dto.getSessionId());
        if (session == null) {
            throw new RuntimeException("场次不存在");
        }
        BigDecimal price = session.getPrice();

        List<SoldSeatDTO> soldSeatDTOS = sessionMapper.selectSoldSeats(dto.getSessionId());

        Set<String> soldSet = new HashSet<>();
        for (SoldSeatDTO sold : soldSeatDTOS) {
            soldSet.add(sold.getSeatRow() + "-" + sold.getSeatCol());
        }

        // 校验用户选的座位有没有被卖
        for (SeatDTO seat : dto.getSeats()) {
            String key = seat.getSeatRow() + "-" + seat.getSeatCol();
            if (soldSet.contains(key)) {
                throw new RuntimeException(seat.getSeatRow() + "排" + seat.getSeatCol() + "座已被购买，请重新选座");
            }
        }

        // 生成订单号
        String orderNo = generateOrderNo();

        // 算总价
        BigDecimal totalPrice = price.multiply(BigDecimal.valueOf(dto.getSeats().size()));

        // 添加数据 订单表
        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setTotalPrice(totalPrice);
        order.setStatus(0);
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        orderMapper.insert(order);
        Order ors =  orderMapper.getTest(orderNo);

//         添加数据 订单细节表
        for (SeatDTO seat : dto.getSeats()) {
            OrderItem item = new OrderItem();
            item.setOrderId(ors.getId());
            item.setSessionId(dto.getSessionId());
            item.setSeatRow(seat.getSeatRow());
            item.setSeatCol(seat.getSeatCol());
            item.setPrice(price);
            orderItemMapper.insertItem(item);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("orderNo", orderNo);
        result.put("totalPrice", totalPrice);
        result.put("orderId", ors.getId());

        // 创建订单成功 → 放入 Redis ZSET 延迟队列，15min 后自动取消
        long expireAt = System.currentTimeMillis() + 15 * 60 * 1000L;
        redisTemplate.opsForZSet().add("order:timeout", String.valueOf(ors.getId()), expireAt);

        return result;
    }

    /**
     * 支付
     * @param orderNo
     */
    @Transactional
    public void setOrder(String orderNo) {
        // 根据订单号查订单
        Order order = orderMapper.updaOrder(orderNo);

        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (order.getStatus() == 1) {
            throw new RuntimeException("订单已支付，请勿重复支付");
        }
        if (order.getStatus() != 0) {
            throw new RuntimeException("订单状态异常，无法支付");
        }

        // 支付成功 → 从延迟队列移除
        redisTemplate.opsForZSet().remove("order:timeout", String.valueOf(order.getId()));
        // 改成已支付
        order.setStatus(1);
        order.setPayTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        orderMapper.updateById(order);
    }

    private String generateOrderNo() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }
}