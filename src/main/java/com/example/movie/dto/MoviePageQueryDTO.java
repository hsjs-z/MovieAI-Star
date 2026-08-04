package com.example.movie.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 作者: 枷
 * 时间:2026/7/22 下午3:29
 * 描述:。。。
 */
@Data
public class MoviePageQueryDTO implements Serializable {
    private int page;
    private int pageSize;

    // 电影名称
    private String name;

    /** 状态：1-上映，0-下架 */
    private Integer status;
}
