package com.example.movie.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 作者: 枷
 * 时间:2026/8/1 下午3:15
 * 描述:排片查看详细
 */
@Data
public class SessionDetailDTO {
    private Long id;
    private Long movieId;
    private String movieName;
    private String posterUrl;
    private Long hallId;
    private String hallName;
    private String location;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BigDecimal price;
    private Integer languageVersion; // 0-2D, 1-3D ...
    private Integer status;          // 1可售 0停售 2已结束
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
