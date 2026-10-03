package com.example.movie.controller.user;

import com.example.movie.bedto.HallPlus;
import com.example.movie.common.result.Result;
import com.example.movie.dto.SoldSeatDTO;
import com.example.movie.service.UserHallService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
@Tag(name = "用户端抢座")
public class UserHallController {
    @Autowired
    private UserHallService uhService;

    @GetMapping("/hall/plus/{sessionId}")
    @Operation(summary = "用户端 提供场次的座位布局")
    public Result<HallPlus> getplus(@PathVariable Long sessionId){
        HallPlus list = uhService.getplus(sessionId);
        return Result.success(list);
    }

    @PostMapping("/hall/seat")
    @Operation(summary = "用户端 抢座")
    public Result goToSeat(@RequestBody SoldSeatDTO dto,
                           @RequestAttribute("currentUserId") Long userId) {
        uhService.goToSeat(dto, userId);
        return Result.success();
    }

    @PostMapping("/hall/seat/yes")
    @Operation(summary = "用户端 提交订单")
    public Result<Map<String, Object>> seatYes(@RequestBody SoldSeatDTO dto,
                                               @RequestAttribute("currentUserId") Long userId) {
        return Result.success(uhService.seatYes(dto, userId));
    }

    @PostMapping("/hall/seat/release")
    @Operation(summary = "用户端 释放已锁座位（再次点击）")
    public Result releaseSeat(@RequestBody SoldSeatDTO dto,
                               @RequestAttribute("currentUserId") Long userId) {
        uhService.releaseSeat(dto, userId);
        return Result.success();
    }
}