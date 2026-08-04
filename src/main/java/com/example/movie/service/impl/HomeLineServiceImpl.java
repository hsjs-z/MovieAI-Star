package com.example.movie.service.impl;

import com.example.movie.dto.HomeLineDTO;
import com.example.movie.mapper.HomeLineMapper;
import com.example.movie.service.HomeLineService;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 作者: 枷
 * 时间:2026/8/1 下午1:04
 * 描述:。。。
 */
@Service
public class HomeLineServiceImpl implements HomeLineService {
    @Autowired
    private HomeLineMapper homeLineMapper;

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
}
