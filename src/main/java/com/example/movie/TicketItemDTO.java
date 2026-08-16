package com.example.movie;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 作者: 枷
 * 时间:2026/8/14 下午9:50
 * 描述:。。。
 */
@Data
public class TicketItemDTO {
    private Integer seatRow;
    private Integer seatCol;
    private BigDecimal price;        // 单张票价
}
