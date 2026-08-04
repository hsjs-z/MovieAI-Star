package com.example.movie.service;

import com.example.movie.common.result.PageResult;
import com.example.movie.dto.MovieDTO;
import com.example.movie.dto.MoviePageQueryDTO;
import com.example.movie.dto.MovieUtilsDTO;

import java.util.List;

/**
 * 作者: 枷
 * 时间:2026/7/22 下午3:35
 * 描述:。。。
 */
public interface MovieService {
    PageResult getMoviePage(MoviePageQueryDTO pageQueryDTO);

    void addMovie(MovieDTO movieDTO);

    MovieDTO getMovieById(Long id);

    void delMovie(List<Long> ids);

    void updateMovie(MovieDTO movieDTO);

    List<MovieUtilsDTO> getCinemasByMovie(Long movieId);
}
