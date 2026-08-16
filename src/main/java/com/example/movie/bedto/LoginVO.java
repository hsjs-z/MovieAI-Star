package com.example.movie.bedto;

import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * 作者: 枷
 * 时间:2026/8/11 下午3:14
 * 描述:登录成功返回
 */
@Component
@Data
public class LoginVO {
    private String token;
    private Integer role;
    private String username;
}
