package com.example.movie.service;

import com.example.movie.bedto.OrderCreateDTO;
import com.example.movie.common.result.PageResult;
import com.example.movie.dto.OrderDetailDTO;
import com.example.movie.dto.OrderPageQueryDTO;

import java.util.Map;

/**
 * 作者: 枷
 * 时间:2026/7/30 上午11:07
 * 描述:。。。
 */
public interface OrderService {
    PageResult getOrderPage(OrderPageQueryDTO dto);

    void updateOrderStatus(Long id);

    void cancelOrder(Long id, Long currentUserId);

    void payOrder(Long id);

    void beOrder(Long id);

    OrderDetailDTO getOrderDetail(Long id);

    Map<String, Object> createOrder(OrderCreateDTO orderCreateDTO, Long userId);

    void setOrder(String orderNo);
}