package com.example.movie.controller.admin;

import com.example.movie.common.result.PageResult;
import com.example.movie.common.result.Result;
import com.example.movie.dto.SessionAddDTO;
import com.example.movie.dto.SessionDetailDTO;
import com.example.movie.dto.SessionPageQueryDTO;
import com.example.movie.dto.SoldSeatDTO;
import com.example.movie.service.SessionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 作者: 枷
 * 时间:2026/7/26 下午8:50
 * 描述:。。。
 */
@RestController
@RequestMapping("/session")
@Slf4j
@Api(tags = "排片管理")
public class SessionController {
    @Autowired
    private SessionService sessionService;
    @GetMapping
    @ApiOperation("条件查询排片")
    public Result getSession(SessionPageQueryDTO pageQueryDTO){
        log.info("查询排片信息{}", pageQueryDTO);
        PageResult pageResult =  sessionService.getSessionPage(pageQueryDTO);
        return Result.success(pageResult);
    }

    @PostMapping
    @ApiOperation("新增排片")
    public Result addSession(@RequestBody SessionAddDTO sessionaddDTO){
        log.info("新增排片{}", sessionaddDTO);
        sessionService.addSession(sessionaddDTO);
        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation("根据id查询排片")
    public Result getSessionById(@PathVariable Long id){
        log.info("根据id查询排片{}", id);
        SessionAddDTO DTO = sessionService.getById(id);
        return Result.success(DTO);
    }

    @PutMapping
    @ApiOperation("根据id修改排片")
    public Result updateSession(@RequestBody SessionAddDTO sessionaddDTO){
        log.info("根据id修改排片{}", sessionaddDTO);
        sessionService.updateSession(sessionaddDTO);
        return Result.success();
    }

    @PutMapping("/status/{id}/{status}")//？？？？
    @ApiOperation("根据id修改状态")
    public Result updateSessionStatus(@PathVariable Long id,@PathVariable Integer status) {
        log.info("根据id修改排片状态{}{}", status,id);
        sessionService.updaStatus(id, status);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @ApiOperation("根据id删除排片")
    public Result deleteSession(@PathVariable Long id) {
        log.info("根据id删除排片{}", id);
        sessionService.deleteSession(id);
        return Result.success();
    }

    @GetMapping("/detail")
    @ApiOperation("查询排片详情")
    public Result<SessionDetailDTO> detail(@RequestParam Long sessionId) {
        log.info("查询排片详情{}", sessionId);
        return Result.success(sessionService.getSessionDetail(sessionId));
    }

    @GetMapping("/seats")
    @ApiOperation("查询已售座位")
    public Result<List<SoldSeatDTO>> soldSeats(@RequestParam Long sessionId) {
        log.info("查询已售座位{}", sessionId);
        return Result.success(sessionService.getSoldSeats(sessionId));
    }

}
