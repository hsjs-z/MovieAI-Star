package com.example.movie.controller.user;

import com.example.movie.bedto.CinemaSession;
import com.example.movie.bedto.SearMovie;
import com.example.movie.bedto.Trailer;
import com.example.movie.common.result.Result;
import com.example.movie.service.UserHomeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 作者: 枷
 * 时间:2026/8/11 下午4:00
 * 描述:。。。
 */
@RestController
@RequestMapping("/users")
@Slf4j
@Tag(name = "用户端")
public class UserHomeController {
    @Autowired
    private UserHomeService uhService;

    @GetMapping("/home")
    @Operation(summary = "用户端首页")
    public Result<List<SearMovie>> home(){
        List<SearMovie> list = uhService.home();
        return Result.success(list);
    }

    @GetMapping("/home/Trailer")
    @Operation(summary = "用户端首页 预告电影")
    public Result<List<Trailer>> homeTrailer(){
        List<Trailer> list = uhService.homeTrailer();
        return Result.success(list);
    }

    @GetMapping("/home/Top100")
    @Operation(summary = "用户端首页 Top100电影")
    public Result<List<SearMovie>> homeTop100(){
        List<SearMovie> list = uhService.homeTop100();
        return Result.success(list);
    }

    //据id返回用户端电影详细数据在movie

    @GetMapping("/plus/{id}")
    @Operation(summary = "用户端首页 电影院与排片")
    public Result<List<CinemaSession>> plus(@PathVariable Long id){
        List<CinemaSession> list = uhService.plus(id);
        return Result.success(list);
    }

}