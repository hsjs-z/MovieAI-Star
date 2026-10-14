package com.example.movie.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.movie.entity.ChatHistory;
import com.example.movie.mapper.ChatHistoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MysqlChatHistoryRepository implements ChatHistoryRepository {

    private final ChatHistoryMapper chatHistoryMapper;

    @Override
    public void save(String type, String chatId, Long userId) {
        Long count = chatHistoryMapper.selectCount(
                new LambdaQueryWrapper<ChatHistory>()
                        .eq(ChatHistory::getType, type)
                        .eq(ChatHistory::getChatId, chatId)
                        .eq(ChatHistory::getUserId, userId)
        );
        if (count > 0) {
            return;
        }
        chatHistoryMapper.insert(ChatHistory.builder()
                .type(type)
                .chatId(chatId)
                .userId(userId)
                .build());
    }

    @Override
    public List<String> getChatIds(String type, Long userId) {
        return chatHistoryMapper.selectList(
                new LambdaQueryWrapper<ChatHistory>()
                        .eq(ChatHistory::getType, type)
                        .eq(ChatHistory::getUserId, userId)
                        .select(ChatHistory::getChatId)
        ).stream().map(ChatHistory::getChatId).toList();
    }
}