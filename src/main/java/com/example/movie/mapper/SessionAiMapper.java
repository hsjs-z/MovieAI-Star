package com.example.movie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.movie.entity.Session;
import org.apache.ibatis.annotations.Mapper;

/**
 * 作者: 枷
 * 时间:2026/10/6
 * 描述:Session表的AI Mapper
 */
@Mapper
public interface SessionAiMapper extends BaseMapper<Session> {
}