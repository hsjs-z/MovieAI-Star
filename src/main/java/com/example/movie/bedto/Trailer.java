package com.example.movie.bedto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 作者: 枷
 * 时间:2026/8/11 下午11:22
 * 描述:。。。
 */
@Data
public class Trailer {
    private Long id;
    private String name;
    private String posterUrl;
    private BigDecimal score;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;
}
