package com.example.movie.bedto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 作者: 枷
 * 时间:2026/8/14 下午2:31
 * 描述:。。。
 */
@Data
public class OrderItem {
    /** 订单ID */
    private Long orderId;

    /** 场次ID */
    private Long sessionId;

    /** 座位排号 */
    private Integer seatRow;

    /** 座位列号 */
    private Integer seatCol;

    /** 单张票价 */
    private BigDecimal price;
}
