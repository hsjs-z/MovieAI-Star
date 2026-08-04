package com.example.movie.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 作者: 枷
 * 时间:2026/8/1 下午5:14
 * 描述:。。。
 */
@Data
public class UserStatDTO {
    private BigDecimal totalSpent;  // 累计消费（已支付订单金额总和）
    private Integer orderCount;     // 已支付订单数量
}
