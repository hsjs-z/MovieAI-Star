package com.example.movie.service;

import com.example.movie.common.result.PageResult;
import com.example.movie.dto.UserDTO;
import com.example.movie.dto.UserDetailDTO;
import com.example.movie.dto.UserPageQueryDTO;

/**
 * 作者: 枷
 * 时间:2026/7/29 上午10:58
 * 描述:。。。
 */
public interface UserService {
    PageResult getUser(UserPageQueryDTO dto);

    UserDTO getById(Long id);

    void updateUser(UserDTO dto);

    void updateUserStatus(Long id, Integer status);

    void updatepassword(Long id);

    void deleteUser(Long id);

    UserDetailDTO getUserDetail(Long userId);
}
