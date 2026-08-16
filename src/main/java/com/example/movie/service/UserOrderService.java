package com.example.movie.service;

import com.example.movie.bedto.UserOrderDetailDTO;
import com.example.movie.bedto.UserOrderListDTO;

import java.util.List;

/**
 * 作者: 枷
 * 时间:2026/8/14 下午9:48
 * 描述:。。。
 */
public interface UserOrderService {
    List<UserOrderListDTO> getOrderList(Long userId);

    UserOrderDetailDTO getUserOrder(String orderNo, Long userId);
}
