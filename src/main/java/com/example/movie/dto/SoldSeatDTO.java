package com.example.movie.dto;

import lombok.Data;

/**
 * 作者: 枷
 * 时间:2026/8/1 下午3:23
 * 描述:已售座位
 */
@Data
public class SoldSeatDTO {
    private Integer seatRow;
    private Integer seatCol;
    private String orderNo;
    private Integer orderStatus;
}
