package com.example.movie.service.impl;

import com.example.movie.bedto.HallPlus;
import com.example.movie.bedto.OrderCreateDTO;
import com.example.movie.bedto.SeatDTO;
import com.example.movie.dto.SoldSeatDTO;
import com.example.movie.entity.SeatLock;
import com.example.movie.mapper.UserHallMapper;
import com.example.movie.service.OrderService;
import com.example.movie.service.UserHallService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserHallServiceImpl implements UserHallService {
    private final UserHallMapper uhMapper;
    private final StringRedisTemplate redisTemplate;
    private final OrderService orderService;

    @Value("classpath:lua/releaseLock.lua")
    private Resource releaseLockLua;

    @Value("classpath:lua/verifyLock.lua")
    private Resource verifyLockLua;

    @Value("classpath:lua/batchLock.lua")
    private Resource batchLockLua;

    private DefaultRedisScript<Long> releaseLockScript;
    private DefaultRedisScript<Long> verifyLockScript;
    private DefaultRedisScript<Long> batchLockScript;

    @PostConstruct
    public void initScripts() throws IOException {
        releaseLockScript = new DefaultRedisScript<>();
        releaseLockScript.setScriptText(readResource(releaseLockLua));
        releaseLockScript.setResultType(Long.class);

        verifyLockScript = new DefaultRedisScript<>();
        verifyLockScript.setScriptText(readResource(verifyLockLua));
        verifyLockScript.setResultType(Long.class);

        batchLockScript = new DefaultRedisScript<>();
        batchLockScript.setScriptText(readResource(batchLockLua));
        batchLockScript.setResultType(Long.class);
    }

    private String readResource(Resource resource) throws IOException {
        try (var is = resource.getInputStream()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    @Override
    public HallPlus getplus(Long sessionId) {
        HallPlus dto = uhMapper.getplus(sessionId);

        if (dto == null) {
            throw new RuntimeException("场次不存在");
        }

        if (dto.getSeatLayout() == null || dto.getSeatLayout().isEmpty()) {
            int capacity = dto.getCapacity();
            int cols = 10;
            int rows = (int) Math.ceil(capacity * 1.0 / cols);
            dto.setSeatLayout("{\"rows\":" + rows + ", \"cols\":" + cols + "}");
        }

        return dto;
    }

    // 抢座（批量原子加锁：一次 Lua 调用全部成功，或全部回滚）
    @Override
    public void goToSeat(SoldSeatDTO dto, Long userId) {
        Long sessionId = dto.getSessionId();
        if (sessionId == null) {
            throw new RuntimeException("场次ID不能为空");
        }

        List<SeatDTO> seats = resolveSeats(dto);

        List<String> keys = new ArrayList<>();
        for (SeatDTO seat : seats) {
            Integer seatRow = seat.getSeatRow();
            Integer seatCol = seat.getSeatCol();

            if (seatRow == null || seatRow == 0 || seatCol == null || seatCol == 0) {
                throw new RuntimeException("座位号不能为空且不能为0");
            }

            keys.add("seat:" + sessionId + ":" + seatRow + ":" + seatCol);
        }

        Long result = redisTemplate.execute(
                batchLockScript,
                keys,
                String.valueOf(userId),
                "300"
        );

        if (result == null) {
            throw new RuntimeException("加锁异常");
        }
        if (result < 0) {
            int failIndex = (int) (-result);
            SeatDTO failSeat = seats.get(failIndex - 1);
            throw new RuntimeException(failSeat.getSeatRow() + "排" + failSeat.getSeatCol() + "座已被占用");
        }

        log.info("用户[{}]批量锁了[{}]个座位", userId, seats.size());
    }

    // 释放座位锁（批量）—— Lua 原子归属验证后释放
    @Override
    public void releaseSeat(SoldSeatDTO dto, Long userId) {
        Long sessionId = dto.getSessionId();
        if (sessionId == null) {
            throw new RuntimeException("场次ID不能为空");
        }

        List<SeatDTO> seats = resolveSeats(dto);

        for (SeatDTO seat : seats) {
            Integer seatRow = seat.getSeatRow();
            Integer seatCol = seat.getSeatCol();

            if (seatRow == null || seatRow == 0 || seatCol == null || seatCol == 0) {
                throw new RuntimeException("座位参数非法");
            }

            String key = "seat:" + sessionId + ":" + seatRow + ":" + seatCol;

            Long result = redisTemplate.execute(
                    releaseLockScript,
                    Collections.singletonList(key),
                    String.valueOf(userId)
            );

            if (result == null || result == 0L) {
                throw new RuntimeException("你还没锁座位，或座位锁已过期");
            }
            if (result == -1L) {
                throw new RuntimeException("座位不是你锁的，不能释放");
            }
        }

        log.info("用户[{}]批量释放了[{}]个座位的锁", userId, seats.size());
    }

    @Override
    public Map<String, Object> seatYes(SoldSeatDTO dto, Long userId) {
        Long sessionId = dto.getSessionId();
        if (sessionId == null) {
            throw new RuntimeException("场次ID不能为空");
        }

        List<SeatDTO> seats = resolveSeats(dto);

        // ① 批量验证每个锁的归属（全通过才往下走）
        List<String> lockedKeys = new ArrayList<>();
        for (SeatDTO seat : seats) {
            String key = "seat:" + sessionId + ":" + seat.getSeatRow() + ":" + seat.getSeatCol();

            Long verify = redisTemplate.execute(
                    verifyLockScript,
                    Collections.singletonList(key),
                    String.valueOf(userId)
            );

            if (verify == null || verify == 0L) {
                throw new RuntimeException("座位锁已失效，请重新选座");
            }
            if (verify == -1L) {
                throw new RuntimeException("座位不是你锁的，请重新选座");
            }

            lockedKeys.add(key);
        }

        // ② DB 全部成功（一个订单 + 批量 seat_lock）
        OrderCreateDTO orderCreateDTO = new OrderCreateDTO();
        orderCreateDTO.setSessionId(sessionId);
        orderCreateDTO.setSeats(seats);

        Map<String, Object> order = orderService.createOrder(orderCreateDTO, userId);
        Long orderId = (Long) order.get("orderId");

        LocalDateTime now = LocalDateTime.now();
        for (SeatDTO seat : seats) {
            SeatLock seatLock = new SeatLock();
            seatLock.setSessionId(sessionId);
            seatLock.setSeatRow(seat.getSeatRow());
            seatLock.setSeatCol(seat.getSeatCol());
            seatLock.setUserId(userId);
            seatLock.setOrderId(orderId);
            seatLock.setExpireTime(now.plusMinutes(10));
            seatLock.setCreateTime(now);
            uhMapper.insertLock(seatLock);
        }

        // ③ DB 全成功，才批量释放 Redis 锁
        for (String key : lockedKeys) {
            redisTemplate.execute(
                    releaseLockScript,
                    Collections.singletonList(key),
                    String.valueOf(userId)
            );
        }

        log.info("用户[{}]提交订单[{}]，[{}]个座位落库成功", userId, orderId, seats.size());
        return order;
    }

    private List<SeatDTO> resolveSeats(SoldSeatDTO dto) {
        if (dto.getSeats() != null && !dto.getSeats().isEmpty()) {
            return dto.getSeats();
        }
        SeatDTO single = new SeatDTO();
        single.setSeatRow(dto.getSeatRow());
        single.setSeatCol(dto.getSeatCol());
        return Collections.singletonList(single);
    }
}