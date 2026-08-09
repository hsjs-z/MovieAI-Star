package com.example.movie.service.impl;

import com.example.movie.common.result.PageResult;
import com.example.movie.dto.MovieDTO;
import com.example.movie.dto.MoviePageQueryDTO;
import com.example.movie.dto.MovieUtilsDTO;
import com.example.movie.entity.Movie;
import com.example.movie.mapper.MovieMapper;
import com.example.movie.mapper.SessionMapper;
import com.example.movie.service.MovieService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 作者: 枷
 * 时间:2026/7/22 下午3:36
 * 描述:。。。
 */
@Service
@Slf4j
public class MovieServiceImpl implements MovieService {
    @Autowired
    private MovieMapper movieMapper;
    @Autowired
    private SessionMapper sessionMapper;

    /**
     * 根据条件查询电影分页列表
     * @param pageQueryDTO
     * @return
     */
    public PageResult getMoviePage(MoviePageQueryDTO pageQueryDTO) {
        PageHelper.startPage(pageQueryDTO.getPage(), pageQueryDTO.getPageSize());
        Page<MovieDTO> page = movieMapper.getMoviepage(pageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 添加电影
     * @param movieDTO
     */
    public void addMovie(MovieDTO movieDTO) {
        Movie movie = new Movie();
        BeanUtils.copyProperties(movieDTO, movie);
        LocalDateTime now = LocalDateTime.now();

        movie.setStatus(0);
        movie.setCreateTime(now);
        movie.setUpdateTime(now);

        movieMapper.addMovie(movie);
    }
    /**
     * 根据id查询电影
     * @param id
     * @return
     */
    public MovieDTO getMovieById(Long id) {
        if (id == null) {
            throw new RuntimeException("id不能为空");
        }
        Movie movie = movieMapper.getMovieById(id);
        MovieDTO movieDTO = new MovieDTO();
        BeanUtils.copyProperties(movie, movieDTO);
        return movieDTO;
    }

    /**
     * 根据id数组删除电影
     * @param ids
     */
    @Transactional
    public void delMovie(List<Long> ids) {
        for (Long id : ids) {
            Movie movie = movieMapper.getMovieById(id);
            if (movie.getStatus() == 1) {
                throw new RuntimeException("电影放映中，不能删除");
            }
        }
        movieMapper.delMovie(ids);
    }
    /**
     * 修改电影
     * @param movieDTO
     */
    public void updateMovie(MovieDTO movieDTO) {
        Movie movie = new Movie();
        BeanUtils.copyProperties(movieDTO, movie);
        movie.setUpdateTime(LocalDateTime.now());
        movieMapper.updateMovie(movie);
    }

    /**
     * 根据电影id查询影院
     * @param movieId
     * @return
     */
    public List<MovieUtilsDTO> getCinemasByMovie(Long movieId) {
        List<MovieUtilsDTO> list = sessionMapper.selcinemove(movieId);
        // 如果没有任何门店（单店且 location 全为 NULL），返回一个默认值
        if (list.isEmpty()) {
            MovieUtilsDTO defaultCinema = new MovieUtilsDTO();
            defaultCinema.setLocation("本影院");
            list.add(defaultCinema);
        }
        return list;
    }

    /**
     * 查询电影名称id
     * @return
     */
    public List<MovieDTO> getMovieidName() {
        List<MovieDTO> list = movieMapper.getMovieidName();
        return list;
    }
}
