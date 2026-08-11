package com.example.movie.controller.Test;

import com.example.movie.bedto.PassWord;
import com.example.movie.common.result.Result;
import com.example.movie.entity.User;
import com.example.movie.mapper.UserMapper;
import com.example.movie.service.impl.UserServiceImpl;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserServiceImpl userService;

    @PostMapping("/register")
    @ApiOperation("注册")
    public Result register(@RequestBody User user) {
        userService.newUser(user);
        return Result.success();
    }

    @PostMapping
    public Result<String> login(@RequestBody PassWord passWord) {
        // 1. 验证用户名密码
        User user = userMapper.searUser(passWord.getUsername());
        if (user == null) {
            return Result.error("用户名或密码错误");
        }

        boolean ok = BCrypt.checkpw(passWord.getPassword(), user.getPassword());
        if (!ok) {
            return Result.error("用户名或密码错误");
        }

        // 2. 生成token返回
//        String token = jwtUtil.generateToken(user.getId(), user.getUsername());
//        return Result.success(token);
        return Result.success("登录成功");
    }
}
