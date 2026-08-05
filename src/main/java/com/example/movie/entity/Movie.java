package com.example.movie.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 作者: 枷
 * 时间:2026/7/22 下午2:41
 * 描述:。。。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Movie implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 电影ID */
    private Long id;

    /** 电影名称 */
    private String name;

    /** 导演 */
    private String director;

    /** 主演 */
    private String cast;

    /** 类型（多个用逗号分隔） */
    private String typeCodes;

    /** 制片国家/地区 */
    private Integer countryCode;

    /** 语言 */
    private Integer languageCode;

    /** 片长（分钟） */
    private Integer duration;

    /** 上映日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;

    /** 海报URL */
    private String posterUrl;

    /** 简介 */
    private String description;

    /** 评分（0-10） */
    private BigDecimal score;

    /** 状态：1-上映，0-下架 */
    private Integer status;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime createTime;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime updateTime;
}
