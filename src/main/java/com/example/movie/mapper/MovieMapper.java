package com.example.movie.mapper;

import com.example.movie.dto.MovieDTO;
import com.example.movie.dto.MoviePageQueryDTO;
import com.example.movie.entity.Movie;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 作者: 枷
 * 时间:2026/7/22 下午3:38
 * 描述:。。。
 */
@Mapper
public interface MovieMapper {
/**
 * 分页查询电影信息
 *
 * @param pageQueryDTO 电影分页查询条件对象，包含分页参数和查询条件
 * @return Page<MovieDTO> 分页查询结果，包含电影数据传输对象列表和分页信息
 */
    Page<MovieDTO> getMoviepage(MoviePageQueryDTO pageQueryDTO);

/**
 * 添加电影的方法
 * @param movies 要添加的电影对象
 */
    void addMovie(Movie movies);

/**
 * 根据电影ID查询电影信息
 * @param id 电影的唯一标识符
 * @return 返回对应ID的电影对象，如果未找到则可能返回null
 */
    @Select("SELECT * FROM movie WHERE id = #{id}")
    Movie getMovieById(Long id);

/**
 * 删除电影的方法
 * @param ids 包含要删除的电影ID的列表
 */
    void delMovie(List<Long> ids);  // 根据传入的ID列表删除对应的电影记录

/**
 * 更新电影信息的方法
 * @param movie 包含更新后电影信息的Movie对象
 */
    void updateMovie(Movie movie);

    @Select("SELECT id,name FROM movie where status = 1")
    List<MovieDTO> getMovieidName();
}
