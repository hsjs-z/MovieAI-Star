package com.example.movie.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 作者: 枷
 * 时间:2026/7/31 下午3:51
 * 描述:关联的票务明细
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketDTO {
    private Long itemId;
    private String movieName;
    private String posterUrl;       // 电影海报
    private String hallName;
    private String location;        // 门店
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer seatRow;
    private Integer seatCol;
    private BigDecimal price;
}
