package com.example.movie.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 作者: 枷
 * 时间:2026/7/25 下午10:57
 * 描述:。。。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HallDTO {
    /** 影厅ID */
    private Long id;

    /** 影厅名称（如：1号厅、IMAX厅） */
    private String name;

    /** 座位总数 */
    private Integer capacity;

    /** 门店位置（如：万达广场店），单店可留空 */
    private String location;
}
