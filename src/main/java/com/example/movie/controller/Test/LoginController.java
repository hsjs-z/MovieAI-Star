package com.example.movie.controller.Test;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.movie.Utils.JwtUtil;
import com.example.movie.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 作者: 枷
 * 时间:2026/8/2 下午4:39
 * 描述:。。。
 */
@RestController
@RequestMapping("/login")
@Slf4j
public class LoginController {
//    @Autowired
//    private JwtUtil jwtUtil;
//
//    @PostMapping
//    public Result<String> login() {
//        // 2. 生成token返回
//        String token = jwtUtil.generateToken(1L, "豪住");
//        return Result.success(token);
//    }
}
