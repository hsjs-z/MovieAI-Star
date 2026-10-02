package com.example.movie.controller.user;

import com.example.movie.bedto.UserOrderDetailDTO;
import com.example.movie.bedto.UserOrderListDTO;
import com.example.movie.common.result.Result;
import com.example.movie.service.OrderService;
import com.example.movie.service.UserOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 作者: 枷
 * 时间:2026/8/14 下午9:46
 * 描述:。。。
 */
@RestController
@RequestMapping("/users/orders")
@Slf4j
@Api(tags = "用户端 订单接口")
public class UserOrderController {
    @Autowired
    private UserOrderService userOrderService;
    @Autowired
    private OrderService orderService;

    @GetMapping
    @ApiOperation("获取用户订单列表")
    public Result<List<UserOrderListDTO>> getOrderList(@RequestAttribute("currentUserId") Long userId) {
        List<UserOrderListDTO> list = userOrderService.getOrderList(userId);
        return Result.success(list);
    }

    @GetMapping("plus")
    @ApiOperation("订单详细")
    public Result<UserOrderDetailDTO> getOrderDetail(@RequestParam String orderNo,
                                                     @RequestAttribute("currentUserId") Long userId) {
        UserOrderDetailDTO detail = userOrderService.getUserOrder(orderNo, userId);
        return Result.success(detail);
    }

    @PostMapping("/cancel")
    @ApiOperation("用户端 取消订单")
    public Result cancelOrder(@RequestParam Long orderId,
                               @RequestAttribute("currentUserId") Long currentUserId) {
        orderService.cancelOrder(orderId, currentUserId);
        return Result.success();
    }

}