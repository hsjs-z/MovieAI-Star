package com.example.movie.mapper;

import com.example.movie.bedto.CinemaSession;
import com.example.movie.entity.Movie;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 作者: 枷
 * 时间:2026/8/11 下午4:10
 * 描述:。。。
 */
@Mapper
public interface UserHomeMapper {
    @Select("select * from movie where status = 1")
    List<Movie> home();

    @Select("select * from movie where status = 2")
    List<Movie> homeTrailer();

    @Select("select * from movie order by create_time desc; ")
    List<Movie> homeTop100();

    List<CinemaSession> plus(Long id);
}