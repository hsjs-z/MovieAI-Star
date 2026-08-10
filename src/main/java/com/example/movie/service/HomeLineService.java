package com.example.movie.service;

import com.example.movie.dto.DailyRevenueDTO;
import com.example.movie.dto.HomeLineDTO;
import com.example.movie.dto.OrderStatusCountDTO;

import java.util.List;

/**
 * 作者: 枷
 * 时间:2026/8/1 下午1:04
 * 描述:。。。
 */
public interface HomeLineService {
    HomeLineDTO gethomeline();

    List<DailyRevenueDTO> getWeeklyRevenue();

    List<OrderStatusCountDTO> getTodayOrderStatus();
}
