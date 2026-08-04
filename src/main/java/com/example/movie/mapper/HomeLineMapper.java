package com.example.movie.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;

/**
 * 作者: 枷
 * 时间:2026/8/1 下午1:06
 * 描述:。。。
 */
@Mapper
public interface HomeLineMapper {
    @Select("SELECT COUNT(*) FROM user")
    Long countTotalUsers();

    @Select("SELECT COUNT(*) FROM user WHERE DATE(create_time) = CURDATE()")
    Long countTodayNewUsers();

    @Select("SELECT COUNT(*) FROM `order` WHERE DATE(create_time) = CURDATE()")
    Long countTodayOrders();

    @Select("SELECT COALESCE(SUM(total_price), 0) FROM `order` WHERE status = 1 AND DATE(pay_time) = CURDATE()")
    BigDecimal sumTodayRevenue();

    @Select("SELECT COUNT(*) FROM movie WHERE status = 1")
    Long countActiveMovies();

    @Select("SELECT COUNT(*) FROM session WHERE DATE(start_time) = CURDATE()")
    Long countTodaySessions();
}
