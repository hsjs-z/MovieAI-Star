package com.example.movie.service;

import com.example.movie.common.result.PageResult;
import com.example.movie.entity.Notice;

/**
 * 作者: 枷
 * 时间:2026/8/1 下午1:38
 * 描述:。。。
 */
public interface NoticeService {
    PageResult getNoticePage(Integer page, Integer pageSize);
    void addNotice(Notice notice);
    void updateNotice(Notice notice);
    void deleteNotice(Long id);
}
