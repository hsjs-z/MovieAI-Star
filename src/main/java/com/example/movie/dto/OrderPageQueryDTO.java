package com.example.movie.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * 作者: 枷
 * 时间:2026/7/30 上午10:46
 * 描述:。。。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderPageQueryDTO {
    @Schema(description = "订单编号")
    private String orderNo;

    @Schema(description = "用户姓名")
    private String username;

    @Schema(description = "订单状态：0待支付,1已支付,2已取消,3已退款")
    private Integer status;

    @Schema(description = "下单起始时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startDate;

    @Schema(description = "下单结束时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endDate;

    @Schema(description = "页码", defaultValue = "1")
    private Integer page = 1;

    @Schema(description = "每页条数", defaultValue = "10")
    private Integer pageSize = 10;
}
