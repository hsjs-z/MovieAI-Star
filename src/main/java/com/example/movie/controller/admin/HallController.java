package com.example.movie.controller.admin;

import com.example.movie.common.result.PageResult;
import com.example.movie.common.result.Result;
import com.example.movie.dto.HallDTO;
import com.example.movie.dto.HallPageQueryDTO;
import com.example.movie.service.HallService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 作者: 枷
 * 时间:2026/7/25 下午7:57
 * 描述:。。。
 */
@RestController
@RequestMapping("/hall")
@Slf4j
@Api(tags = "影厅管理")
public class HallController {
    @Autowired
    private HallService hallService;

    @GetMapping
    @ApiOperation("条件查询影厅")
    public Result getHall(HallPageQueryDTO pageQueryDTO) {
        log.info("查询影厅信息{}", pageQueryDTO);
        PageResult pageResult = hallService.getHallPage(pageQueryDTO);
        return Result.success(pageResult);
    }

    @PostMapping
    @ApiOperation("添加影厅")
    public Result addHall(@RequestBody HallDTO hallDTO) {
        log.info("添加影厅{}", hallDTO);
        hallService.addHall(hallDTO);
        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation("根据id查询影厅")
    public Result getHallById(@PathVariable Long id) {
        log.info("查询影厅信息{}", id);
        HallDTO hallDTO = hallService.getHallById(id);
        return Result.success(hallDTO);
    }

    @PutMapping
    @ApiOperation("修改影厅")
    public Result updateHall(@RequestBody HallDTO hallDTO) {
        log.info("修改影厅{}", hallDTO);
        hallService.updateHall(hallDTO);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除影厅")
    public Result deleteHall(@PathVariable Long id) {
        log.info("删除影厅{}", id);
        hallService.deleteHall(id);
        return Result.success();
    }
}
