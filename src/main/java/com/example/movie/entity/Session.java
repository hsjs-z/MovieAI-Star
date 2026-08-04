package com.example.movie.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 作者: 枷
 * 时间:2026/7/26 下午4:37
 * 描述:。。。
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Session {
    /** 场次ID */
    private Long id;

    /** 电影ID */
    private Long movieId;

    /** 影厅ID */
    private Long hallId;

    /** 放映开始时间 */
    private LocalDateTime startTime;

    /** 放映结束时间 */
    private LocalDateTime endTime;

    /** 票价 */
    private BigDecimal price;

    /** 语言版本：0-2D,1-3D,2-IMAX2D... */
    private Integer languageVersion;

    /** 状态：1可售, 0停售, 2已结束 */
    private Integer status;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
