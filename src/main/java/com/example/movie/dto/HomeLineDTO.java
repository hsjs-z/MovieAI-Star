package com.example.movie.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 作者: 枷
 * 时间:2026/8/1 下午1:01
 * 描述:。。。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HomeLineDTO {
    private Long totalUsers;        // 总用户数
    private Long todayNewUsers;     // 今日新增用户
    private Long todayOrders;       // 今日订单数
    private BigDecimal todayRevenue; // 今日收入
    private Long activeMovies;      // 上映中电影数
    private Long todaySessions;
}
