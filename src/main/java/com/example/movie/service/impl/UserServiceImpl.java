package com.example.movie.service.impl;

import com.example.movie.common.result.PageResult;
import com.example.movie.dto.UserDTO;
import com.example.movie.dto.UserDetailDTO;
import com.example.movie.dto.UserPageQueryDTO;
import com.example.movie.dto.UserStatDTO;
import com.example.movie.entity.User;
import com.example.movie.mapper.OrderMapper;
import com.example.movie.mapper.UserMapper;
import com.example.movie.service.UserService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 作者: 枷
 * 时间:2026/7/29 上午10:58
 * 描述:。。。
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private OrderMapper orderMapper;

    /**
     * @param dto
     * 分页
     * @return
     */
    public PageResult getUser(UserPageQueryDTO dto) {
        PageHelper.startPage(dto.getPage(), dto.getPageSize());
        Page<UserDTO> page = userMapper.getPageUser(dto);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * @param id
     * 根据id查询用户信息
     * @return
     */
    public UserDTO getById(Long id) {
        if (id == null) {
            throw new RuntimeException("用户ID不能为空");
        }
        User user = userMapper.getById(id);
        UserDTO dto = new UserDTO();
        BeanUtils.copyProperties(user, dto);
        return dto;
    }

    /**
     * @param dto
     * 新增用户
     */
    @Transactional
    public void updateUser(UserDTO dto) {
        if (dto == null) {
            throw new RuntimeException("用户信息不能为空");
        }
        User user = new User();
        BeanUtils.copyProperties(dto, user);
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateUser(user);
    }

    /**
     * @param status
     * @param id
     * 修改用户状态
     */
    public void updateUserStatus(Long id, Integer status) {
        if (status == null || id == null) {
            throw new RuntimeException("用户状态或用户ID不能为空");
        }
        User user = userMapper.getById(id);
        if (user.getUsername().equals("admin")){
            throw new RuntimeException("管理员不能被禁用");
        }
        user.setStatus(status);
        userMapper.updatestatus(user);

    }

    /**
     * 重置密码
     */
    public void updatepassword(Long id) {
        User user = userMapper.getById(id);
        user.setPassword("123456");
        userMapper.updatepassword(user);
    }

    /**
     * 删除用户
     */
    public void deleteUser(Long id) {
        //管理员不能删除
        User user = userMapper.getById(id);
        if (user.getUsername().equals("admin")) {
            throw new RuntimeException("管理员不能被删除");
        }
        //如有订单在 用户不能删除
        int count =  orderMapper.selUser(id);
        if (count > 0) {
            throw new RuntimeException("该用户有订单，不能删除");
        }
        userMapper.deleteUserById(id);
    }

    /**
     * 获取用户详情
     */
    public UserDetailDTO getUserDetail(Long userId) {
        // 1. 查用户基本信息
        User user = userMapper.getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        UserDetailDTO dto = new UserDetailDTO();
        BeanUtils.copyProperties(user, dto);

        // 2. 查订单统计
        UserStatDTO stat = orderMapper.selectUserStat(userId);
        if (stat != null) {
            dto.setTotalSpent(stat.getTotalSpent());
            dto.setOrderCount(stat.getOrderCount());
        }

        return dto;
    }

    /**
     * 注册用户
     * @param user
     */
    public void newUser(User user) {
        List<User> getu =  userMapper.getOnly();
        if (getu.size() != 0){
            for (User u : getu) {
                if (u.getUsername().equals(user.getUsername())) {
                    throw new RuntimeException("用户名已存在");
                }
                if (u.getPhone().equals(user.getPassword())){
                    throw new RuntimeException("手机号已存在");
                }
            }

            //加密
            String bc = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
            user.setPassword(bc);

            user.setRole(1);
            user.setStatus(1);

            userMapper.insertUser(user);
        }
    }
}
