package com.example.movie.controller.admin;

import com.example.movie.common.result.PageResult;
import com.example.movie.common.result.Result;
import com.example.movie.dto.MovieDTO;
import com.example.movie.dto.MoviePageQueryDTO;
import com.example.movie.dto.MovieUtilsDTO;
import com.example.movie.service.MovieService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 作者: 枷
 * 时间:2026/7/22 下午2:24
 * 描述:。。。
 */
@RestController
@RequestMapping("/movie")
@Slf4j
@Api(tags = "电影管理")
public class MovieController {
    @Autowired
    private MovieService movieService;
    @GetMapping
    @ApiOperation("条件查询电影")
    public Result getMovie(MoviePageQueryDTO PageQueryDTO) {
        log.info("查询电影信息{}", PageQueryDTO);
        PageResult pageResult = movieService.getMoviePage(PageQueryDTO);
        return Result.success(pageResult);
    }

    @GetMapping("/idname")
    @ApiOperation("查询电影名跟id")
    public Result getMovieidName() {
        log.info("查询影厅名");
        List<MovieDTO> list = movieService.getMovieidName();
        return Result.success(list);
    }


    @PostMapping
    @ApiOperation("添加电影")
    public Result addMovie(@RequestBody MovieDTO movieDTO) {
        log.info("添加电影信息{}", movieDTO);
        movieService.addMovie(movieDTO);
        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation("编辑按钮 根据id查询电影信息")
    public Result getMovieById(@PathVariable Long id) {
        log.info("查询电影信息{}", id);
        MovieDTO movieDTO = movieService.getMovieById(id);
        return Result.success(movieDTO);
    }

    @GetMapping("/status/{id}/{status}")
    @ApiOperation("根据id更改电影状态")
    public Result updaById(@PathVariable Long id, @PathVariable Integer status) {
        log.info("查询在映{}  {}", id ,status);
        movieService.updaById(id,status);
        return Result.success();
    }

    @PutMapping
    @ApiOperation("修改按钮")
    public Result updateMovie(@RequestBody MovieDTO movieDTO) {
        log.info("修改电影信息{}", movieDTO);
        movieService.updateMovie(movieDTO);
        return Result.success();
    }

    @DeleteMapping
    @ApiOperation("删除电影")
    public Result delMovie(@RequestParam List<Long> ids) {
        log.info("删除电影信息{}", ids);
        movieService.delMovie(ids);
        return Result.success();
    }

    @GetMapping("/cinema")
    @ApiOperation("根据电影id查询影院信息")
    public Result<List<MovieUtilsDTO>> cinemas(@RequestParam Long movieId) {
        return Result.success(movieService.getCinemasByMovie(movieId));
    }
}
