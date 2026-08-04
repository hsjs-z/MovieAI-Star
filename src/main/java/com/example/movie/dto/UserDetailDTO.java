package com.example.movie.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 作者: 枷
 * 时间:2026/8/1 下午5:08
 * 描述:。。。
 */
@Data
public class UserDetailDTO {
    private Long id;
    private String username;
    private String phone;
    private String avatar;
    private Integer role;        // ADMIN 或 USER
    private Integer status;     // 1正常 0禁用
    private LocalDateTime createTime;
    // 可选统计信息
    private BigDecimal totalSpent;  // 历史总消费金额（已支付订单）
    private Integer orderCount;     // 已完成订单数
}
