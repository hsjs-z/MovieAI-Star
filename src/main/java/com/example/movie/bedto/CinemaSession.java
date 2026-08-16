package com.example.movie.bedto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 作者: 枷
 * 时间:2026/8/12 下午3:30
 * 描述:排片and影院
 */
@Data
public class CinemaSession {
    private Long sessionId;

    /** 放映开始时间 */
    private LocalDateTime startTime;

    /** 放映结束时间 */
    private LocalDateTime endTime;

    /** 票价 */
    private BigDecimal price;

    /** 语言版本：0-2D,1-3D,2-IMAX2D... */
    private Integer languageVersion;

    /** 影厅名称（如：1号厅、IMAX厅） */
    private String name;

    /** 座位总数 */
    private Integer capacity;

    /** 座位布局 json字符串 */
    private String seatLayout;

    /** 门店位置（如：万达广场店），单店可留空 */
    private String location;
}
