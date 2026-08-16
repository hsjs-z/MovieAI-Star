package com.example.movie.bedto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 作者: 枷
 * 时间:2026/8/13 下午1:51
 * 描述:选座
 */
@Data
public class HallPlus {
    private Long sessionId;
    private String movieName;
    private String posterUrl;
    private String hallName;
    private String location;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BigDecimal price;
    private Integer languageVersion;   // 0=2D,1=3D...
    private String seatLayout;         // 座位布局 JSON 字符串
    private Integer capacity;
}
