package com.example.movie.bedto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 作者: 枷
 * 时间:2026/8/16 下午2:56
 * 描述:。。。
 */
@Data
public class NoticeDTO implements Serializable {
    private String title;
    private String content;
    private LocalDateTime updateTime;
}
