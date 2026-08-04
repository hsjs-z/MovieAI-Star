package com.example.movie.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 作者: 枷
 * 时间:2026/7/26 下午4:45
 * 描述:。。。
 */
@Data
public class SessionAddDTO {
    /** 场次ID */
    private Long id;

    @NotNull(message = "电影ID不能为空")
    private Long movieId;

    @NotNull(message = "影厅ID不能为空")
    private Long hallId;

    @NotNull(message = "开始时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @NotNull(message = "结束时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    @NotNull(message = "票价不能为空")
    @DecimalMin(value = "0.01", message = "票价必须大于0")
    private BigDecimal price;

    private Integer languageVersion;

    private Integer status;
}
