package com.example.movie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.movie.entity.Order;
import org.apache.ibatis.annotations.Mapper;

/**
 * 作者: 枷
 * 时间:2026/10/6
 * 描述:Order表的AI Mapper
 */
@Mapper
public interface OrderAiMapper extends BaseMapper<Order> {
}