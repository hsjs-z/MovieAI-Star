package com.example.movie.controller.admin;

import com.example.movie.common.result.PageResult;
import com.example.movie.common.result.Result;
import com.example.movie.dto.HallDTO;
import com.example.movie.dto.HallPageQueryDTO;
import com.example.movie.entity.Hall;
import com.example.movie.service.HallService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 作者: 枷
 * 时间:2026/7/25 下午7:57
 * 描述:。。。
 */
@RestController
@RequestMapping("/hall")
@Slf4j
@Tag(name = "影厅管理")
public class HallController {
    @Autowired
    private HallService hallService;

    @GetMapping
    @Operation(summary = "条件查询影厅")
    public Result getHall(HallPageQueryDTO pageQueryDTO) {
        log.info("查询影厅信息{}", pageQueryDTO);
        PageResult pageResult = hallService.getHallPage(pageQueryDTO);
        return Result.success(pageResult);
    }

    @GetMapping("/idname")
    @Operation(summary = "查询影厅名id，用于前端回显")
    public Result getHallName() {
        log.info("查询影厅名id");
        List<HallDTO> list = hallService.getHallName();
        return Result.success(list);
    }

    @PostMapping
    @Operation(summary = "添加影厅")
    public Result addHall(@RequestBody HallDTO hallDTO) {
        log.info("添加影厅{}", hallDTO);
        hallService.addHall(hallDTO);
        return Result.success();
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据id查询影厅")
    public Result getHallById(@PathVariable Long id) {
        log.info("查询影厅信息{}", id);
        HallDTO hallDTO = hallService.getHallById(id);
        return Result.success(hallDTO);
    }

    @PutMapping
    @Operation(summary = "修改影厅")
    public Result updateHall(@RequestBody HallDTO hallDTO) {
        log.info("修改影厅{}", hallDTO);
        hallService.updateHall(hallDTO);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除影厅")
    public Result deleteHall(@PathVariable Long id) {
        log.info("删除影厅{}", id);
        hallService.deleteHall(id);
        return Result.success();
    }

    @GetMapping("/counts")
    @Operation(summary = "返回影厅")
    public Result<List<Hall>> getcountHall() {
        List<Hall> hall = hallService.getHallCon();
        return Result.success(hall);
    }

}
