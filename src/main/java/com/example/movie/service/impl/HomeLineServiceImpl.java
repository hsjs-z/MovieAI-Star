package com.example.movie.service.impl;

import com.example.movie.dto.DailyRevenueDTO;
import com.example.movie.dto.HomeLineDTO;
import com.example.movie.dto.OrderStatusCountDTO;
import com.example.movie.mapper.HomeLineMapper;
import com.example.movie.mapper.OrderMapper;
import com.example.movie.service.HomeLineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 作者: 枷
 * 时间:2026/8/1 下午1:04
 * 描述:。。。
 */
@Service
public class HomeLineServiceImpl implements HomeLineService {
    @Autowired
    private HomeLineMapper homeLineMapper;
    @Autowired
    private OrderMapper orderMapper;

    /**
     * 获取首页数据
     * @return
     */
    public HomeLineDTO gethomeline() {
        HomeLineDTO dto = new HomeLineDTO();
        dto.setTotalUsers(homeLineMapper.countTotalUsers());
        dto.setTodayNewUsers(homeLineMapper.countTodayNewUsers());
        dto.setTodayOrders(homeLineMapper.countTodayOrders());
        dto.setTodayRevenue(homeLineMapper.sumTodayRevenue());
        dto.setActiveMovies(homeLineMapper.countActiveMovies());
        dto.setTodaySessions(homeLineMapper.countTodaySessions());
        return dto;
    }

    /**
     * 获取近7天收入
     * @return
     */
    public List<DailyRevenueDTO> getWeeklyRevenue() {

        List<DailyRevenueDTO> dbList = orderMapper.selectWeeklyRevenue();
        // 将查询结果转为 Map<日期, 收入>
        Map<String, BigDecimal> map = dbList.stream()
                .collect(Collectors.toMap(DailyRevenueDTO::getDate, DailyRevenueDTO::getRevenue));
        // 补全最近7天
        List<DailyRevenueDTO> result = new ArrayList<>();
        LocalDate today = LocalDate.now();
        for (int i = 6; i >= 0; i--) {
            String date = today.minusDays(i).toString();
            DailyRevenueDTO dto = new DailyRevenueDTO();
            dto.setDate(date);
            dto.setRevenue(map.getOrDefault(date, BigDecimal.ZERO));
            result.add(dto);
        }
        return result;
    }

    /**
     * 获取今日各状态订单数
     * @return
     */
    public List<OrderStatusCountDTO> getTodayOrderStatus() {
        List<OrderStatusCountDTO> list = orderMapper.selectTodayOrderStatus();
        // 给每个状态加上中文标签
        Map<Integer, String> statusMap = new HashMap<>();
        statusMap.put(0, "待支付");
        statusMap.put(1, "已支付");
        statusMap.put(2, "已取消");
        statusMap.put(3, "已退款");
        for (OrderStatusCountDTO dto : list) {
            dto.setLabel(statusMap.getOrDefault(dto.getStatus(), "未知"));
        }
        return list;
    }
}
