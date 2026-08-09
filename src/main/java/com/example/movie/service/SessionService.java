package com.example.movie.service;

import com.example.movie.common.result.PageResult;
import com.example.movie.dto.SessionAddDTO;
import com.example.movie.dto.SessionDetailDTO;
import com.example.movie.dto.SessionPageQueryDTO;
import com.example.movie.dto.SoldSeatDTO;

import java.util.List;

/**
 * 作者: 枷
 * 时间:2026/7/26 下午8:59
 * 描述:。。。
 */
public interface SessionService {
    PageResult getSessionPage(SessionPageQueryDTO pageQueryDTO);

    void addSession(SessionAddDTO sessionaddDTO);

    SessionAddDTO getById(Long id);

    void updateSession(SessionAddDTO sessionaddDTO);

    void updaStatus(Long id, Integer status);

    void deleteSession(Long id);

    SessionDetailDTO getSessionDetail(Long sessionId);

    List<SoldSeatDTO> getSoldSeats(Long sessionId);
}
