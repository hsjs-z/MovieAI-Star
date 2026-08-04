package com.example.movie.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 作者: 枷
 * 时间:2026/7/26 下午4:45
 * 描述:。。。
 */
@Data
public class SessionDTO {
    /** 场次ID */
    private Long id;

    /** 电影ID */
    private Long movieId;

    /** 电影名称 */
    private String movieName;

    /** 影厅ID */
    private Long hallId;

    /** 影厅名称 */
    private String hallName;

    /** 影厅位置 */
    private String location;

    /** 放映开始时间 */
    private LocalDateTime startTime;

    /** 放映结束时间 */
    private LocalDateTime endTime;

    /** 票价 */
    private BigDecimal price;

    /** 语言版本：0-2D,1-3D,2-IMAX2D... */
    private Integer languageVersion;

    /** 状态：1可售, 0停售, 2已结束 */
    private Integer status;
}
