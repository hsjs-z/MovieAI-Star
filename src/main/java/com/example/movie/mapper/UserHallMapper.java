package com.example.movie.mapper;

import com.example.movie.bedto.HallPlus;
import com.example.movie.entity.SeatLock;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserHallMapper {
    HallPlus getplus(Long sessionId);

    void insertLock(SeatLock seatLock);

    List<SeatLock> selectLocksByOrderId(@Param("orderId") Long orderId);

    int deleteLockByOrderId(@Param("orderId") Long orderId);
}