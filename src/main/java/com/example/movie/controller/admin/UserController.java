package com.example.movie.controller.admin;

import com.example.movie.common.result.PageResult;
import com.example.movie.common.result.Result;
import com.example.movie.dto.UserDTO;
import com.example.movie.dto.UserDetailDTO;
import com.example.movie.dto.UserPageQueryDTO;
import com.example.movie.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 作者: 枷
 * 时间:2026/7/29 上午10:29
 * 描述:。。。
 */
@RestController
@RequestMapping("/user")
@Slf4j
@Api(tags = "用户管理")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping
    @ApiOperation("条件查询用户")
    public Result getUser(UserPageQueryDTO dto){
        log.info("查询用户信息{}",dto);
        PageResult pageResult = userService.getUser(dto);
        return Result.success(pageResult);
    }

    @GetMapping("/{id}")
    @ApiOperation("根据id查询用户")
    public Result getUserById(@PathVariable Long id) {
        log.info("查询用户信息{}", id);
        UserDTO dto = userService.getById(id);
        return Result.success(dto);
    }

    @PutMapping
    @ApiOperation("修改用户信息")
    public Result updateUser(@RequestBody UserDTO dto){
        log.info("修改用户信息{}",dto);
        userService.updateUser(dto);
        return Result.success();
    }

    @PutMapping("/status/{status}")
    @ApiOperation("修改用户状态")
    public Result updateUserStatus(@PathVariable Integer status, Long id){
        log.info("修改用户状态{}{}",status,id);
        userService.updateUserStatus(status,id);
        return Result.success();
    }

    @PutMapping("/password/{id}")
    @ApiOperation("重置用户密码")
    public Result updateUserPassword(@PathVariable Long id){
        userService.updatepassword(id);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除用户")
    public Result deleteUser(@PathVariable Long id) {
        log.info("删除用户信息{}", id);
        userService.deleteUser(id);
        return Result.success();
    }

    @GetMapping("/detail")
    @ApiOperation("查询用户详情")
    public Result<UserDetailDTO> detail(@RequestParam Long userId) {
        return Result.success(userService.getUserDetail(userId));
    }

}
