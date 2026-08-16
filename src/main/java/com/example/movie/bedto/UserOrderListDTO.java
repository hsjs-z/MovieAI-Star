package com.example.movie.bedto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 作者: 枷
 * 时间:2026/8/14 下午10:04
 * 描述:。。。
 */
@Data
public class UserOrderListDTO {
    private Long orderId;
    private String orderNo;          // 订单编号
    private String movieName;        // 电影名（方便列表展示）
    private String posterUrl;        // 电影海报
    private String hallName;         // 影厅名称
    private String location;         // 门店位置
    private LocalDateTime startTime; // 放映时间
    private BigDecimal totalPrice;   // 总金额
    private Integer status;          // 0待支付 1已支付 2已取消 3已退款
    private LocalDateTime createTime;
}
