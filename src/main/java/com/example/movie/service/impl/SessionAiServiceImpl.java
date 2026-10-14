package com.example.movie.service.impl;

import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.example.movie.entity.Session;
import com.example.movie.mapper.SessionAiMapper;
import com.example.movie.service.SessionAiService;
import org.springframework.stereotype.Service;

/**
 * 作者: 枷
 * 时间:2026/10/6
 * 描述:Session表的AI Service实现
 */
@Service
public class SessionAiServiceImpl extends ServiceImpl<SessionAiMapper, Session> implements SessionAiService {
}