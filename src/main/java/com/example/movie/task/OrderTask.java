package com.example.movie.task;

import com.example.movie.mapper.SessionMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 作者: 枷
 * 时间:2026/8/17 上午8:49
 * 描述:定时任务（取消未支付订单已改用 Redis ZSET 延迟队列）
 */
@Component
@Slf4j
public class OrderTask {
    @Autowired
    private SessionMapper sessionMapper;

    // 场次结束标记（这个无法用延迟队列替代，因为场次结束时间分散在各个时间点）
    @Scheduled(cron = "0 0/5 * * * ?")
    public void finishSession() {
        LocalDateTime time = LocalDateTime.now();
        int sum = sessionMapper.ifsession(time);
        if (sum > 0) {
            log.info("场次放映时间结束，已处理数量：{}", sum);
        }
    }
}