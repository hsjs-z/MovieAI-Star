package com.example.movie.aidomain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 作者: 枷
 * 时间:2026/10/6
 * 描述:场次票源信息DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionTicketInfo implements Serializable {

    private Long sessionId;

    private String movieName;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;

    private String hallName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime startTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime endTime;

    private BigDecimal price;

    private Integer totalSeats;

    private Integer soldSeats;

    private Integer remainingSeats;

    private Boolean available;

    private String statusDesc;
}