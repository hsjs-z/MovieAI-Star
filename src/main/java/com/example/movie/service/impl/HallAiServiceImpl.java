package com.example.movie.service.impl;

import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.example.movie.entity.Hall;
import com.example.movie.mapper.HallAiMapper;
import com.example.movie.service.HallAiService;
import org.springframework.stereotype.Service;

/**
 * 作者: 枷
 * 时间:2026/10/6
 * 描述:Hall表的AI Service实现
 */
@Service
public class HallAiServiceImpl extends ServiceImpl<HallAiMapper, Hall> implements HallAiService {
}