package com.example.movie.service;

import com.example.movie.bedto.HallPlus;
import com.example.movie.dto.SoldSeatDTO;

import java.util.Map;

public interface UserHallService {
    HallPlus getplus(Long sessionId);

    void goToSeat(SoldSeatDTO dto, Long userId);

    Map<String, Object> seatYes(SoldSeatDTO dto, Long userId);

    void releaseSeat(SoldSeatDTO dto, Long userId);
}