package com.example.movie.mapper;

import com.example.movie.dto.*;
import com.example.movie.entity.Order;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 作者: 枷
 * 时间:2026/7/30 上午11:08
 * 描述:。。。
 */
@Mapper
public interface OrderMapper {
    Page<OrderDTO> getPageOrder(OrderPageQueryDTO dto);

    @Select("select * from `order` where id = #{id}")
    Order getById(Long id);

    void updateById(Order order);

    OrderDetailDTO selById(Long id);

    List<TicketDTO> selTicById(Long id);

    @Select("SELECT COALESCE(SUM(total_price), 0) AS totalSpent, " +
            "COUNT(*) AS orderCount " +
            "FROM `order` " +
            "WHERE user_id = #{userId} AND status = 1")
    UserStatDTO selectUserStat(Long userId);
}
