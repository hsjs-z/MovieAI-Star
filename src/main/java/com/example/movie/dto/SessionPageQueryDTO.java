package com.example.movie.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * 作者: 枷
 * 时间:2026/7/26 下午4:46
 * 描述:。。。
 */
@Data
public class SessionPageQueryDTO {
    private Integer page = 1;
    private Integer pageSize = 10;

    /** 电影名称 */
    private String movieName;

    /** 影厅名称 */
    private String hallName;

    /** 影厅位置 */
    private String location;

    /** 开始时间 */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    /** 结束时间 */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    /** 状态：1-上架，0-下架 */
    private Integer status;
    }
