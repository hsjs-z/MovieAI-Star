package com.example.movie.controller.user;

import com.example.movie.bedto.*;
import com.example.movie.common.result.Result;
import com.example.movie.service.UserHomeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
@Api(tags = "用户端")
public class UserHomeController {
    @Autowired
    private UserHomeService uhService;

    @GetMapping("/home")
    @ApiOperation("用户端首页")
    public Result<List<SearMovie>> home(){
        List<SearMovie> list = uhService.home();
        return Result.success(list);
    }

    @GetMapping("/home/Trailer")
    @ApiOperation("用户端首页 预告电影")
    public Result<List<Trailer>> homeTrailer(){
        List<Trailer> list = uhService.homeTrailer();
        return Result.success(list);
    }

    @GetMapping("/home/Top100")
    @ApiOperation("用户端首页 Top100电影")
    public Result<List<SearMovie>> homeTop100(){
        List<SearMovie> list = uhService.homeTop100();
        return Result.success(list);
    }

    //据id返回用户端电影详细数据在movie

    @GetMapping("/plus/{id}")
    @ApiOperation("用户端首页 电影院与排片")
    public Result<List<CinemaSession>> plus(@PathVariable Long id){
        List<CinemaSession> list = uhService.plus(id);
        return Result.success(list);
    }

    @GetMapping("/hall/plus/{sessionId}")
    @ApiOperation("用户端 选座")
    public Result<HallPlus> getplus(@PathVariable Long sessionId){
        HallPlus list = uhService.getplus(sessionId);
        return Result.success(list);
    }
}
