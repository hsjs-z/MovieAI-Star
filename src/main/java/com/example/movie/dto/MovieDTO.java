package com.example.movie.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 作者: 枷
 * 时间:2026/7/22 下午3:19
 * 描述:电影表DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieDTO implements Serializable {
    private Long id;
    /** 电影名称 */
    private String name;

    /** 类型（多个用逗号分隔） */
    private Integer typeCode;

    /** 片长（分钟） */
    private Integer duration;

    /** 状态：1-上映，0-下架 */
    private Integer status;

    /** 导演 */
    private String director;

    /** 主演 */
    private String cast;

    /** 制片国家/地区 */
    private Integer countryCode;

    /** 上映日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;

    /** 语言 */
    private Integer languageCode;

    /** 海报URL */
    private String posterUrl;

    /** 简介 */
    private String description;

    /** 评分（0-10） */
    private BigDecimal score;
}
