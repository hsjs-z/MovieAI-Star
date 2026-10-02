package com.example.movie.service;

import com.example.movie.bedto.CinemaSession;
import com.example.movie.bedto.SearMovie;
import com.example.movie.bedto.Trailer;

import java.util.List;

/**
 * 作者: 枷
 * 时间:2026/8/11 下午4:08
 * 描述:。。。
 */
public interface UserHomeService {
    List<SearMovie> home();

    List<Trailer> homeTrailer();

    List<SearMovie> homeTop100();

    List<CinemaSession> plus(Long id);
}