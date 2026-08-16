package com.example.movie.mapper;

import com.example.movie.bedto.OrderItem;
import org.apache.ibatis.annotations.Mapper;

/**
 * 作者: 枷
 * 时间:2026/8/14 下午5:04
 * 描述:。。。
 */
@Mapper
public interface OrderItemMapper {
    void insertItem(OrderItem item);
}
