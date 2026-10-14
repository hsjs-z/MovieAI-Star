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
 * 描述:取消订单DTO，userId由系统从登录上下文自动获取
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CancelOrderQuery implements Serializable {

    @ToolParam(required = true, description = "订单ID（纯数字）或订单编号（字符串）")
    private String orderIdOrNo;
}