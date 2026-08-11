package com.example.movie.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 作者: 枷
 * 时间:2026/8/10 上午11:35
 * 描述:图表数据
 */
@Data
public class DailyRevenueDTO {
    private String date;          // 日期 "yyyy-MM-dd"
    private BigDecimal revenue;   // 收入
}
