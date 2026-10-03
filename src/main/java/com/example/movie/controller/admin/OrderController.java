package com.example.movie.controller.admin;

import com.example.movie.bedto.OrderCreateDTO;
import com.example.movie.common.result.PageResult;
import com.example.movie.common.result.Result;
import com.example.movie.dto.OrderDetailDTO;
import com.example.movie.dto.OrderPageQueryDTO;
import com.example.movie.service.OrderService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 作者: 枷
 * 时间:2026/7/30 上午11:06
 * 描述:。。。
 */
@RestController
@RequestMapping("/order")
@Slf4j
@Tag(name = "订单管理")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @GetMapping
    @Operation(summary = "分页查询订单")
    public Result getOrderPage(OrderPageQueryDTO dto) {
        log.info("分页查询订单{}", dto);
        PageResult pageResult = orderService.getOrderPage(dto);
        return Result.success(pageResult);
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询订单详情")
    public Result<OrderDetailDTO> getOrderDetail(@PathVariable Long id) {
        log.info("查询订单详情{}", id);
        OrderDetailDTO orderDetail = orderService.getOrderDetail(id);
        return Result.success(orderDetail);
    }

    @PutMapping("/{id}")
    @Operation(summary = "取消订单")
    public Result updateOrderStatus(@PathVariable Long id) {
        log.info("取消订单{}", id);
        orderService.updateOrderStatus(id);
        return Result.success();
    }

    @PutMapping("/pay/{id}")
    @Operation(summary = "支付订单")
    public Result payOrder(@PathVariable Long id) {
        log.info("支付订单{}", id);
        orderService.payOrder(id);
        return Result.success();
    }

    @PutMapping("/be/{id}")
    @Operation(summary = "订单退款")
    public Result beOrder(@PathVariable Long id) {
        log.info("订单退款{}", id);
        orderService.beOrder(id);
        return Result.success();
    }

    @PostMapping("/create")
    @Operation(summary = "用户端 生成订单")
    public Result<Map<String, Object>> createOrder(@RequestBody OrderCreateDTO orderCreateDTO, @RequestAttribute("currentUserId") Long userId) {
        if (orderCreateDTO.getSessionId() == null) {
            return Result.error("场次ID不能为空");
        }
        if (orderCreateDTO.getSeats() == null || orderCreateDTO.getSeats().isEmpty()) {
            return Result.error("请至少选择一个座位");
        }

        Map<String, Object> result = orderService.createOrder(orderCreateDTO, userId);

        return Result.success(result);
    }

    @GetMapping("/pay")
    @Operation(summary = "模拟支付")
    public Result payOrder(@RequestParam String orderNo) {
        orderService.setOrder(orderNo);
        return Result.success();
    }

}
