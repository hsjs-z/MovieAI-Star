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

    @Select("SELECT COUNT(*) FROM `order` o WHERE o.user_id = #{id}")
    int selUser(Long id);

    // 近7天收入（有数据的日期）
    @Select("SELECT DATE(pay_time) AS date, SUM(total_price) AS revenue " +
            "FROM `order` " +
            "WHERE status = 1 AND pay_time >= DATE_SUB(CURDATE(), INTERVAL 30 DAY) " +
            "GROUP BY DATE(pay_time) ORDER BY date ASC")
    List<DailyRevenueDTO> selectWeeklyRevenue();

    // 今日各状态订单数
    @Select("SELECT status, COUNT(*) AS count " +
            "FROM `order` " +
            "WHERE DATE(create_time) = CURDATE() " +
            "GROUP BY status")
    List<OrderStatusCountDTO> selectTodayOrderStatus();

    void insert(Order order);

    @Select("select * from `order` where order_no = #{orderNo}")
    Order updaOrder(String orderNo);

    @Select("select * from `order` where order_no = #{orderNo}")
    Order getTest(String orderNo);
}
