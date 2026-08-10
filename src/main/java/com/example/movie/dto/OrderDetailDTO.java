package com.example.movie.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 作者: 枷
 * 时间:2026/7/31 下午3:51
 * 描述:基本信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
//获取订单详细dto
public class OrderDetailDTO {
    private Long orderId;
    private String orderNo;
    private Long userId;
    private String username;
    private BigDecimal totalPrice;
    private Integer status;     // 0待支付 1已支付 2已取消 3已退款
    private LocalDateTime createTime;
    private LocalDateTime payTime;
    private LocalDateTime refundTime;
    private List<TicketDTO> tickets;
}
