package com.example.movie.service.impl;

import com.example.movie.bedto.CinemaSession;
import com.example.movie.bedto.SearMovie;
import com.example.movie.bedto.Trailer;
import com.example.movie.entity.Movie;
import com.example.movie.mapper.UserHomeMapper;
import com.example.movie.service.UserHomeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者: 枷
 * 时间:2026/8/11 下午4:09
 * 描述:。。。
 */
@Service
@Slf4j
public class UserHomeImpl implements UserHomeService {
    @Autowired
    private UserHomeMapper uhMapper;

    @Cacheable(value = "homeMovieList", key = "'home'")
    /**
     * 首页电影
     * @return
     */
    public List<SearMovie> home() {
        List<Movie> m = uhMapper.home();
        List<SearMovie> searMovie = new ArrayList<>();

        for (Movie movie : m) {
            SearMovie sm = new SearMovie();
            BeanUtils.copyProperties(movie, sm);
            searMovie.add(sm);
        }
        return searMovie;
    }

    @Cacheable(value = "homeMovieList", key = "'homeTrailer'")
    /**
     * 首页预告
     *
     * @return
     */
    public List<Trailer> homeTrailer() {
        List<Movie> m = uhMapper.homeTrailer();
        List<Trailer> searMovie = new ArrayList<>();

        for (Movie movie : m) {
            Trailer sm = new Trailer();
            BeanUtils.copyProperties(movie, sm);
            searMovie.add(sm);
        }
        return searMovie;
    }

    @Cacheable(value = "homeMovieList", key = "'homeTop100'")
    /**
     * 首页热门100
     * @return
     */
    public List<SearMovie> homeTop100() {
        List<Movie> m = uhMapper.homeTop100();
        List<SearMovie> searMovie = new ArrayList<>();

        for (Movie movie : m) {
            SearMovie sm = new SearMovie();
            BeanUtils.copyProperties(movie, sm);
            searMovie.add(sm);
        }
        return searMovie;
    }

    /**
     * 电影院与排片
     * @return
     */
    public List<CinemaSession> plus(Long id) {
        List<CinemaSession> cs = uhMapper.plus(id);
        return cs;
    }
}