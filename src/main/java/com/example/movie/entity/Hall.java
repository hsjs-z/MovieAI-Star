package com.example.movie.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 作者: 枷
 * 时间:2026/7/25 下午7:32
 * 描述:。。。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Hall implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 影厅ID */
    private Long id;

    /** 影厅名称（如：1号厅、IMAX厅） */
    private String name;

    /** 座位总数 */
    private Integer capacity;

    /** 座位布局 json字符串 */
    private String seatLayout;

    /** 门店位置（如：万达广场店），单店可留空 */
    private String location;

    /** 创建时间 */
    private LocalDateTime createTime;
}
