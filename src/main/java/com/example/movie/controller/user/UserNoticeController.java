package com.example.movie.controller.user;

import com.example.movie.bedto.NoticeDTO;
import com.example.movie.common.result.Result;
import com.example.movie.entity.Notice;
import com.example.movie.mapper.UserNoticeMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者: 枷
 * 时间:2026/8/16 下午3:00
 * 描述:。。。
 */
@RestController
@RequestMapping("/users")
@Slf4j
@Tag(name = "用户端")
public class UserNoticeController {
     @Autowired
     private UserNoticeMapper userNoticeMapper;

    @Cacheable(value = "noticeList", key = "'userNotice'")
     //用户端通知
    @GetMapping("/notice")
    @Operation(summary = "用户端通知")
    public Result<List<NoticeDTO>> userNotice(){
        List<Notice> list =  userNoticeMapper.getLine();
        List<NoticeDTO> noti = new ArrayList<>();

        for (Notice notice : list) {
            NoticeDTO dto = new NoticeDTO();
            BeanUtils.copyProperties(notice, dto);
            noti.add(dto);
        }
        return Result.success(noti);
    }
}
