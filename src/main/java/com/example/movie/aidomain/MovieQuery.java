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
 * 时间:2026/7/22 下午3:19
 * 描述:电影表DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieQuery implements Serializable {
    /** 电影名称 */
    @ToolParam(required = false, description = "电影名称")
    private String name;

    /** 类型（多个用逗号分隔） */
    @ToolParam(required = false, description = "类型: 0-动作, 1-喜剧, 2-剧情, 3-爱情, 4-科幻, 5-悬疑, 6-恐怖, 7-动画, 8-战争, 9-冒险, 10-纪录片, 11-奇幻")
    private String typeCodes;

    /** 片长（分钟） */
    @ToolParam(required = false, description = "片长（分钟）")
    private Integer duration;

    /** 状态：1-上映，0-下架 */
    @ToolParam(required = false, description = "状态：1-上映，0-下架，2-待上映")
    private Integer status;

    /** 导演 */
    @ToolParam(required = false, description = "导演")
    private String director;

    /** 主演 */
    @ToolParam(required = false, description = "主演")
    private String cast;

    /** 制片国家/地区 */
    @ToolParam(required = false, description = "制片国家/地区: 0-内地, 1-欧美,2-日韩")
    private Integer countryCode;

    /** 上映日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @ToolParam(required = false, description = "上映日期")
    private LocalDate releaseDate;

    /** 语言 */
    @ToolParam(required = false, description = "语言: 0-中文, 1-外语")
    private Integer languageCode;
}
