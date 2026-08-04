package com.example.movie.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 作者: 枷
 * 时间:2026/7/22 下午3:29
 * 描述:。。。
 */
@Data
public class HallPageQueryDTO implements Serializable {
    private int page;
    private int pageSize;

    /** 影厅名称（如：1号厅、IMAX厅） */
    private String name;

    /** 门店位置（如：万达广场店），单店可留空 */
    private String location;
}
