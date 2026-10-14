package com.example.movie.aidomain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 作者: 枷
 * 时间:2026/10/11
 * 描述:AI 选座查询返回：场次座位布局 + 不可用座位明细
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionSeatInfo implements Serializable {

    private Long sessionId;

    private String movieName;

    private String hallName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime startTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime endTime;

    private BigDecimal price;

    private Integer rows;

    private Integer cols;

    private Integer capacity;

    private Integer freeSeatCount;

    private Boolean bookable;

    private String statusDesc;

    /** 不可用座位明细（已售/已被他人锁定/你已锁定），其余座位即为可选 */
    private List<SeatState> unavailableSeats;
}
