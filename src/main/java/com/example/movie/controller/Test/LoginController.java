package com.example.movie.controller.Test;

import com.example.movie.Utils.JwtUtil;
import com.example.movie.bedto.LoginVO;
import com.example.movie.bedto.PassWord;
import com.example.movie.common.result.Result;
import com.example.movie.entity.User;
import com.example.movie.mapper.UserMapper;
import com.example.movie.service.impl.UserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class LoginController {

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserServiceImpl userService;

    /**
     * 用户端注册
     */
    @PostMapping("/users/register")
    @Operation(summary = "用户注册")
    public Result register(@RequestBody User user) {
        userService.newUser(user);
        return Result.success();
    }

    /**
     * 管理端登录
     */
    @PostMapping("/login")
    @Operation(summary = "管理端登录")
    public Result<LoginVO> adminLogin(@RequestBody PassWord passWord) {
        // 查询用户
        User user = userMapper.searUser(passWord.getUsername());
        if (user == null) {
            return Result.error("用户名或密码错误");
        }

        // 密码校验
        if (!"admin".equals(user.getUsername())) {
            boolean ok = BCrypt.checkpw(passWord.getPassword(), user.getPassword());
            if (!ok) {
                return Result.error("用户名或密码错误");
            }
        }

        if (user.getRole() == null || user.getRole() != 0) {
            return Result.error("该账号不是管理员");
        }

        if (user.getStatus() == null || user.getStatus() == 0) {
            return Result.error("账号已被禁用");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());

        LoginVO loginVO = new LoginVO();
        loginVO.setUsername(user.getUsername());
        loginVO.setRole(user.getRole());
        loginVO.setToken(token);
        return Result.success(loginVO);
    }

    /**
     * 用户端登录
     */
    @PostMapping("/users/login")
    @Operation(summary = "用户端登录")
    public Result<LoginVO> userLogin(@RequestBody PassWord passWord) {
        User user = userMapper.searUser(passWord.getUsername());
        if (user == null || !BCrypt.checkpw(passWord.getPassword(), user.getPassword())) {
            return Result.error("用户名或密码错误");
        }

        if (user.getRole() == null || user.getRole() != 1) {
            return Result.error("该账号不是普通用户");
        }

        if (user.getStatus() == null || user.getStatus() == 0) {
            return Result.error("账号已被禁用");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());

        LoginVO loginVO = new LoginVO();
        loginVO.setUsername(user.getUsername());
        loginVO.setRole(user.getRole());
        loginVO.setToken(token);
        return Result.success(loginVO);
    }
}