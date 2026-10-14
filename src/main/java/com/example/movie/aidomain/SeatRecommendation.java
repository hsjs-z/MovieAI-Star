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
 * 描述:AI 模糊选座推荐返回：根据偏好推荐的具体座位
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeatRecommendation implements Serializable {

    private Long sessionId;

    private String movieName;

    private String hallName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime startTime;

    private BigDecimal price;

    private Integer rows;

    private Integer cols;

    private Integer freeSeatCount;

    /** 用户传入的偏好原文（便于核对） */
    private String preference;

    /** 处理结果说明：推荐成功 / 错误原因 */
    private String statusDesc;

    /** 推荐理由（如何理解用户偏好） */
    private String reason;

    /** 推荐的具体座位（state 均为"推荐"，可直接用于 lockSeats） */
    private List<SeatState> suggestedSeats;
}
