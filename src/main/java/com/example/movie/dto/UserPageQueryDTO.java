package com.example.movie.dto;

import lombok.Data;

/**
 * 作者: 枷
 * 时间:2026/7/29 上午10:56
 * 描述:。。。
 */
@Data
public class UserPageQueryDTO {
    private Integer page;
    private Integer pageSize;

    /**
     * 用户名
     */
    private String username;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 状态：1-正常，0-禁用
     */
    private Integer status;

}
