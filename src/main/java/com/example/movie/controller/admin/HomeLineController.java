package com.example.movie.controller.admin;

import com.example.movie.common.result.Result;
import com.example.movie.dto.HomeLineDTO;
import com.example.movie.service.HomeLineService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 作者: 枷
 * 时间:2026/8/1 下午1:03
 * 描述:。。。
 */

@RestController
@RequestMapping("/homeline")
@Slf4j
public class HomeLineController {
    @Autowired
    private HomeLineService homeLineService;

    @GetMapping("/line")
    @ApiOperation("首页数据")
    public Result<HomeLineDTO> getHomeLine(){
        return Result.success(homeLineService.gethomeline());
    }
}
