package com.example.movie.mapper;

import com.example.movie.bedto.OrderDetailRawDTO;
import com.example.movie.bedto.UserOrderListDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 作者: 枷
 * 时间:2026/8/14 下午10:09
 * 描述:。。。
 */
@Mapper
public interface UserOrderMapper {
    List<UserOrderListDTO> getOrderList(Long userId);

    List<UserOrderListDTO> getOrderListWithFilter(@Param("userId") Long userId,
                                                  @Param("movieName") String movieName,
                                                  @Param("status") Integer status);

    List<OrderDetailRawDTO> selectOrderDetailByOrderNo(@Param("orderNo") String orderNo,
                                                       @Param("userId") Long userId);
}