package com.example.movie.bedto;

import lombok.Data;

import java.util.List;

/**
 * 作者: 枷
 * 时间:2026/8/14 下午1:54
 * 描述:生成订单前端传参
 */
@Data
public class OrderCreateDTO {
    private Long sessionId;
    private List<SeatDTO> seats;
}
