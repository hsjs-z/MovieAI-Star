package com.example.movie.aidomain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.ai.tool.annotation.ToolParam;

import java.io.Serializable;

/**
 * 作者: 枷
 * 时间:2026/10/6
 * 描述:查询当前用户订单DTO，userId由系统从登录上下文自动获取
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyOrdersQuery implements Serializable {

    @ToolParam(required = false, description = "电影名称，模糊匹配。用户说'火影的订单'就传'火影'")
    private String movieName;

    @ToolParam(required = false, description = "订单状态过滤：0=待支付，1=已支付，2=已取消，3=已退款。不传则查全部")
    private Integer status;
}