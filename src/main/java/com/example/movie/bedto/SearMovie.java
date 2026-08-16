package com.example.movie.bedto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 作者: 枷
 * 时间:2026/8/11 下午3:59
 * 描述:首页展示电影
 */
@Data
public class SearMovie {
    private Long id;
    private String name;
    private String posterUrl;
    private BigDecimal score;
}
