package com.example.movie.controller.admin;

import com.example.movie.common.result.PageResult;
import com.example.movie.common.result.Result;
import com.example.movie.entity.Notice;
import com.example.movie.service.NoticeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 作者: 枷
 * 时间:2026/8/1 下午1:26
 * 描述:。。。
 */

@RestController
@RequestMapping("/notice")
@Slf4j
@Tag(name = "公告管理")
public class NoticeController {
    @Autowired
    private NoticeService noticeService;

    @GetMapping
    @Operation(summary = "首页分页")
    public Result getMovie(@RequestParam(defaultValue = "1") Integer page,@RequestParam(defaultValue = "10") Integer pageSize) {
        log.info("查询首页信息{} {}", page, pageSize);
        PageResult pageResult = noticeService.getNoticePage(page, pageSize);
        return Result.success(pageResult);
    }

    // 新增
    @PostMapping
    @Operation(summary = "新增公告")
    public Result add(@RequestBody Notice notice) {
        log.info("新增公告{}", notice);
        noticeService.addNotice(notice);
        return Result.success();
    }

    // 据id回显
    @GetMapping("/{id}")
    @Operation(summary = "据id回显")
    public Result idselNotice(@PathVariable Long id) {
        log.info("据id回显{}", id);
        Notice notice =  noticeService.idselNotice(id);
        return Result.success(notice);
    }

    // 修改
    @PutMapping
    @Operation(summary = "修改公告")
    public Result update(@RequestBody Notice notice) {
        log.info("修改公告{}", notice);
        noticeService.updateNotice(notice);
        return Result.success();
    }

    // 删除
    @DeleteMapping("/{id}")
    @Operation(summary = "删除公告")
    public Result delete(@PathVariable Long id) {
        log.info("删除公告{}", id);
        noticeService.deleteNotice(id);
        return Result.success();
    }

    @PutMapping("/status/{id}/{status}")
    @Operation(summary = "修改公告状态")
    public Result updaSta(@PathVariable Long id, @PathVariable Integer status) {
        log.info("修改公告状态{}  {}", id, status);
        noticeService.updaSta(id,status);
        return Result.success();
    }


}
