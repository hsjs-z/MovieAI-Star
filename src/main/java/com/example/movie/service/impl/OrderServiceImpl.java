package com.example.movie.service.impl;

import com.example.movie.common.result.PageResult;
import com.example.movie.dto.OrderDTO;
import com.example.movie.dto.OrderDetailDTO;
import com.example.movie.dto.OrderPageQueryDTO;
import com.example.movie.dto.TicketDTO;
import com.example.movie.entity.Order;
import com.example.movie.mapper.OrderMapper;
import com.example.movie.service.OrderService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 作者: 枷
 * 时间:2026/7/30 上午11:07
 * 描述:。。。
 */
@Service
@Slf4j
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderMapper orderMapper;

    /**
     * 获取订单分页
     * @param dto
     * @return
     */
    public PageResult getOrderPage(OrderPageQueryDTO dto) {
        PageHelper.startPage(dto.getPage(), dto.getPageSize());
        Page<OrderDTO> page = orderMapper.getPageOrder(dto);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 取消订单
     * @param id
     */
    public void updateOrderStatus(Long id) {
        Order order = orderMapper.getById(id);
        if (order.getStatus().equals(0)) {
            order.setStatus(2);
            orderMapper.updateById(order);
        }else {
            throw new RuntimeException("订单已支付");
        }
    }

    /**
     * 支付订单
     * @param id
     */
    @Transactional
    public void payOrder(Long id) {
        Order order = orderMapper.getById(id);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (order.getStatus() != 0) {
            throw new RuntimeException("只有待支付订单才能支付");
        }
        // 更新状态 + 支付时间
        order.setStatus(1);
        order.setPayTime(LocalDateTime.now());
        orderMapper.updateById(order);
    }

    /**
     * 退款处理
     * @param id
     */
    @Transactional
    public void delOrder(Long id) {
        Order order = orderMapper.getById(id);
        if (order.getStatus() != 1){
            throw new RuntimeException("只有已支付订单才能退款");
        }
        // 更新状态 + 支付时间
        order.setStatus(3);
        order.setRefundTime(LocalDateTime.now());
        orderMapper.updateById(order);
    }

    /**
     * 获取订单详情
     * @param id
     * @return
     */
    public OrderDetailDTO getOrderDetail(Long id) {
        OrderDetailDTO order = orderMapper.selById(id);
        if (order == null){
            throw new RuntimeException("订单不存在");
        }
        List<TicketDTO> tickets = orderMapper.selTicById(id);
        order.setTickets(tickets);
        log.info("订单详情{}", order);
        log.info("影票信息{}", tickets);
        return order;
    }
}
