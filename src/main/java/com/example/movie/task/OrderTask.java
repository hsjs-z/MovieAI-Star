package com.example.movie.task;

import com.example.movie.mapper.OrderMapper;
import com.example.movie.mapper.SessionMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 作者: 枷
 * 时间:2026/8/17 上午8:49
 * 描述:。。。
 */
@Component
@Slf4j
public class OrderTask {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private SessionMapper sessionMapper;

    @Scheduled(cron = "0 0/5 * * * ?")
    public void cancelUnpaidOrders() {
        log.info("定时任务启动");
        LocalDateTime time = LocalDateTime.now().minusMinutes(15);

        int count = orderMapper.cancelUnpaidOrders(time);
        if (count > 0) {
            log.info("取消未支付订单成功，取消订单数量：{}", count);
        }

        //排片取消
        int sum =  sessionMapper.ifsession(time);
        if(sum>0){
            log.info("电影放映时间结束取消的取消数量：{}", sum);
        }
    }
}
