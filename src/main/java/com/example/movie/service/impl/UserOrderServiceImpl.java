package com.example.movie.service.impl;

import com.example.movie.Utils.DictConvert;
import com.example.movie.dto.TicketItemDTO;
import com.example.movie.bedto.OrderDetailRawDTO;
import com.example.movie.bedto.UserOrderDetailDTO;
import com.example.movie.bedto.UserOrderListDTO;
import com.example.movie.mapper.UserOrderMapper;
import com.example.movie.service.UserOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 作者: 枷
 * 时间:2026/8/14 下午9:48
 * 描述:。。。
 */
@Service
@Slf4j
public class UserOrderServiceImpl implements UserOrderService {
    @Autowired
    private UserOrderMapper userOrderMapper;

    /**
     * 获取用户订单列表
     *
     * @param userId
     * @return
     */
    public List<UserOrderListDTO> getOrderList(Long userId) {
        List<UserOrderListDTO> list = userOrderMapper.getOrderList(userId);
        if (list == null) {
            return null;
        }
        return list;

    }

    public List<UserOrderListDTO> getOrderListWithFilter(Long userId, String movieName, Integer status) {
        List<UserOrderListDTO> list = userOrderMapper.getOrderListWithFilter(userId, movieName, status);
        if (list != null) {
            list.forEach(dto -> dto.setStatusName(DictConvert.orderStatusToName(dto.getStatus())));
        }
        return list;
    }


    /**
     * 获取用户订单详情
     *
     * @param orderNo
     * @param userId
     * @return
     */
    public UserOrderDetailDTO getUserOrder(String orderNo, Long userId) {
        List<OrderDetailRawDTO> rawList = userOrderMapper.selectOrderDetailByOrderNo(orderNo, userId);
        if (rawList.isEmpty()) {
            throw new RuntimeException("订单不存在");
        }

        // 取第一条作为订单主信息和电影 影厅 场次信息
        OrderDetailRawDTO first = rawList.get(0);
        UserOrderDetailDTO dto = new UserOrderDetailDTO();
        BeanUtils.copyProperties(first, dto);
        dto.setLanguageVersion(convertLanguageVersion(first.getLanguageVersion()));

        // 组装票务明细
        List<TicketItemDTO> tickets = rawList.stream().map(raw -> {
            TicketItemDTO t = new TicketItemDTO();
            t.setSeatRow(raw.getSeatRow());
            t.setSeatCol(raw.getSeatCol());
            t.setPrice(raw.getTicketPrice());
            return t;
        }).collect(Collectors.toList());
        dto.setTickets(tickets);

        return dto;
    }

/**
 *  * 语言版本转换
 */
    private String convertLanguageVersion(Integer code) {
        // 检查输入是否为null，如果是则直接返回"未知"
        if (code == null) return "未知";
        // 使用switch语句根据不同的code值返回对应的语言版本描述
        switch (code) {
            case 0:  // 当code为0时，返回"2D"
                return "2D";
            case 1:  // 当code为1时，返回"3D"
                return "3D";
            case 2:  // 当code为2时，返回"IMAX2D"
                return "IMAX2D";
            case 3:
                return "IMAX3D";
            default:
                return "未知";
        }
    }
}