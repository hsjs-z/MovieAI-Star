package com.example.movie.dto;

import lombok.Data;

/**
 * 作者: 枷
 * 时间:2026/8/10 上午11:36
 * 描述:图表数据
 */
@Data
public class OrderStatusCountDTO {
    private Integer status;   // 状态码
    private String label;     // 状态中文名
    private Long count;       // 数量
}
