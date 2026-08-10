package com.example.movie.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 作者: 枷
 * 时间:2026/7/30 上午11:23
 * 描述:。。。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
//获取分页dto
public class OrderDTO {
    private Long id;
    private String orderNo;
    private Long userId;
    private String username;   // 来自 user 表
    private BigDecimal totalPrice;
    private Integer status;    // 0待支付 1已支付 2已取消 3已退款
    private LocalDateTime createTime;
    private LocalDateTime payTime;
}
