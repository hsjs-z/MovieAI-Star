package com.example.movie.aidomain;

import com.example.movie.bedto.SeatDTO;
import lombok.Data;

import java.util.List;

/**
 * 作者: 枷
 * 时间:2026/10/11
 * 描述:AI 抢座/下单参数。单座传 seatRow+seatCol，多座传 seats 列表
 */
@Data
public class BookSeatQuery {
    private Long sessionId;
    private Integer seatRow;
    private Integer seatCol;
    private List<SeatDTO> seats;
}
