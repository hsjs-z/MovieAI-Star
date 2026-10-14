package com.example.movie.aidomain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.ai.tool.annotation.ToolParam;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * 作者: 枷
 * 时间:2026/10/6
 * 描述:电影场次统一查询DTO，覆盖上映日期、最早场次、某日余票、某场次余票等所有场次相关查询
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieSessionQuery implements Serializable {

    @ToolParam(required = true, description = "电影名称，支持模糊匹配")
    private String movieName;

    @ToolParam(required = false, description = "放映日期，格式yyyy-MM-dd。不传则从今天起往后查所有日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate showDate;

    @ToolParam(required = false, description = "时间段筛选：上午、下午、晚上")
    private String timePeriod;

    @ToolParam(required = false, description = "具体时刻如14:00、18:30，用于精确匹配小时")
    private String specificTime;

    @ToolParam(required = false, description = "场次ID，传入后只查询这一场的详细余票情况")
    private Long sessionId;

    @ToolParam(required = false, description = "是否只返回最早可观看的那一场，true=只返回时间最早的一场有票场次，用于回答'最快能看哪一场'")
    private Boolean earliestOnly;

    @ToolParam(required = false, description = "是否只返回有票的场次，默认true")
    private Boolean onlyAvailable;
}