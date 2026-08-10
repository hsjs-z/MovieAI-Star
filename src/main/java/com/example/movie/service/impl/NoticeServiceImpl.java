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
        notice.setUpdateTime(LocalDateTime.now());
        noticeMapper.updateById(notice);
    }

    /**
     * 删除公告
     * @param id
     */
    public void deleteNotice(Long id) {
        Notice notice = noticeMapper.selectById(id);
        if (notice == null) {
            throw new RuntimeException("公告不存在或已被删除");
        }
        noticeMapper.deleteById(id);
    }

    /**
     * 根据id查询公告
     * @param id
     * @return
     */
    public Notice idselNotice(Long id) {
        Notice exist = noticeMapper.selectById(id);
        if (exist == null) {
            throw new RuntimeException("公告不存在");
        }
        return exist;
    }
}
