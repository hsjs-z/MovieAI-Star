package com.example.movie.service.impl;

import com.example.movie.common.result.PageResult;
import com.example.movie.entity.Notice;
import com.example.movie.mapper.NoticeMapper;
import com.example.movie.service.NoticeService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 作者: 枷
 * 时间:2026/8/1 下午1:38
 * 描述:。。。
 */
@Service
public class NoticeServiceImpl implements NoticeService {
    @Autowired
    private NoticeMapper noticeMapper;

    /**
     * 获取公告分页
     * @param page
     * @param pageSize
     * @return
     */
    public PageResult getNoticePage(Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        Page<Notice> pages = noticeMapper.getNoticePage();
        return new PageResult(pages.getTotal(), pages.getResult());
    }

    /**
     * 添加公告
     * @param notice
     */
    public void addNotice(Notice notice) {
        notice.setCreateTime(LocalDateTime.now());
        notice.setUpdateTime(LocalDateTime.now());
        noticeMapper.insert(notice);
    }

    /**
     * 编辑公告
     * @param notice
     */
    @Override
    public void updateNotice(Notice notice) {
        Notice exist = noticeMapper.selectById(notice.getId());
        if (exist == null) {
            throw new RuntimeException("公告不存在");
        }
        notice.setUpdateTime(LocalDateTime.now());
        noticeMapper.updateById(notice);
    }

    /**
     * 删除公告
     * @param id
     */
    public void deleteNotice(Long id) {
        int rows = noticeMapper.deleteById(id);
        if (rows == 0) {
            throw new RuntimeException("公告不存在或已被删除");
        }
    }
}
