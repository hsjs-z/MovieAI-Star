package com.example.movie.service.impl;

import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.example.movie.entity.Order;
import com.example.movie.mapper.OrderAiMapper;
import com.example.movie.service.OrderAiService;
import org.springframework.stereotype.Service;

/**
 * 作者: 枷
 * 时间:2026/10/6
 * 描述:Order表的AI Service实现
 */
@Service
public class OrderAiServiceImpl extends ServiceImpl<OrderAiMapper, Order> implements OrderAiService {
}