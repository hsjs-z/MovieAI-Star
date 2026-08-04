package com.example.movie.controller.admin;

import com.example.movie.common.result.PageResult;
import com.example.movie.common.result.Result;
import com.example.movie.dto.OrderDetailDTO;
import com.example.movie.dto.OrderPageQueryDTO;
import com.example.movie.mapper.OrderMapper;
import com.example.movie.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 作者: 枷
 * 时间:2026/7/30 上午11:06
 * 描述:。。。
 */
@RestController
@RequestMapping("/order")
@Slf4j
@Api(tags = "订单管理")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @GetMapping
    @ApiOperation("分页查询订单")
    public Result getOrderPage(OrderPageQueryDTO dto){
        log.info("分页查询订单{}" , dto);
        PageResult pageResult = orderService.getOrderPage(dto);
        return Result.success(pageResult);
    }

    @GetMapping("/{id}")
    @ApiOperation("查询订单详情")
    public Result<OrderDetailDTO> getOrderDetail(@PathVariable Long id){
        log.info("查询订单详情{}", id);
        OrderDetailDTO orderDetail = orderService.getOrderDetail(id);
        return Result.success(orderDetail);
    }

    @PutMapping("/{id}")
    @ApiOperation("取消订单")
    public Result updateOrderStatus(@PathVariable Long id){
        log.info("取消订单{}" , id);
        orderService.updateOrderStatus(id);
        return Result.success();
    }

    @PutMapping("/pay/{id}")
    @ApiOperation("支付订单")
    public Result payOrder(@PathVariable Long id) {
        log.info("支付订单{}", id);
        orderService.payOrder(id);
        return Result.success();
    }

    @PutMapping("/del/{id}")
    @ApiOperation("删除订单")
    public Result delOrder(@PathVariable Long id) {
        log.info("删除订单{}", id);
        orderService.delOrder(id);
        return Result.success();
    }

}
