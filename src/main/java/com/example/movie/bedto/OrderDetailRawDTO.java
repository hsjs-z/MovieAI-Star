package com.example.movie.bedto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 作者: 枷
 * 时间:2026/8/14 下午11:10
 * 描述:中间查询结果 DTO
 */
@Data
public class OrderDetailRawDTO {
    private Long orderId;
    private String orderNo;
    private BigDecimal totalPrice;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime payTime;
    private String movieName;
    private String posterUrl;
    private String hallName;
    private String location;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer languageVersion;
    private Integer seatRow;
    private Integer seatCol;
    private BigDecimal ticketPrice;
}
