package com.example.movie.aidomain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 作者: 枷
 * 时间:2026/10/6 下午7:55
 * 描述:AI工具返回的电影VO，所有编码字段已转换为中文名称
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieAIVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    private String name;

    private String director;

    private String cast;

    private String typeNames;

    private String countryName;

    private String languageName;

    private Integer duration;

    private LocalDate releaseDate;

    private BigDecimal score;

    private String statusName;
}