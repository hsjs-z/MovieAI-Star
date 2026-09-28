package com.example.movie.bedto;

import com.example.movie.dto.TicketItemDTO;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 作者: 枷
 * 时间:2026/8/14 下午9:49
 * 描述:。。。
 */
@Data
public class UserOrderDetailDTO {
    // 订单基本信息
    private Long orderId;
    private String orderNo;          // 订单编号
    private BigDecimal totalPrice;   // 总金额
    private Integer status;          // 0待支付 1已支付 2已取消 3已退款
    private LocalDateTime createTime;
    private LocalDateTime payTime;

    // order_item 联查的第一条记录，如果订单只有一场则直接放这里
    private String movieName;
    private String posterUrl;        // 海报
    private String hallName;         // 影厅名称
    private String location;         // 门店
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String languageVersion;  // 2D/3D等（前端根据数字转中文）

    // 票务明细 如果订单可能包含多张票，放一个列表
    private List<TicketItemDTO> tickets;
}