package com.example.movie.service;

import com.example.movie.common.result.PageResult;
import com.example.movie.dto.HallDTO;
import com.example.movie.dto.HallPageQueryDTO;

import java.util.List;

/**
 * 作者: 枷
 * 时间:2026/7/25 下午10:10
 * 描述:。。。
 */
public interface HallService {
    PageResult getHallPage(HallPageQueryDTO pageQueryDTO);

    void addHall(HallDTO hallDTO);

    void updateHall(HallDTO hallDTO);

    HallDTO getHallById(Long id);

    void deleteHall(Long id);

    List<HallDTO> getHallName();
}
