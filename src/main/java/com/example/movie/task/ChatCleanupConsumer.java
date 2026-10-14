package com.example.movie.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.movie.entity.ChatHistory;
import com.example.movie.entity.ChatMessage;
import com.example.movie.mapper.ChatHistoryMapper;
import com.example.movie.mapper.ChatMessageMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * 作者: 枷
 * 时间:2026/10/6 下午9:10
 * 描述:Redis ZSET 延时队列消费者：最后一条消息后3分钟自动清理 chat_history + chat_message
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class ChatCleanupConsumer {

    private final StringRedisTemplate redisTemplate;
    private final ChatHistoryMapper chatHistoryMapper;
    private final ChatMessageMapper chatMessageMapper;

    private static final String CLEANUP_ZSET = "chat:cleanup";

    @Scheduled(fixedRate = 1000)
    public void scanExpiredChats() {
        long now = System.currentTimeMillis();

        Set<String> members = redisTemplate.opsForZSet()
                .rangeByScore(CLEANUP_ZSET, 0, now);

        if (members == null || members.isEmpty()) {
            return;
        }

        for (String member : members) {
            try {
                cleanup(member);
            } catch (Exception e) {
                log.error("对话[{}]清理失败，将重试", member, e);
            }
        }
    }

    private void cleanup(String member) {
        Long removed = redisTemplate.opsForZSet().remove(CLEANUP_ZSET, member);
        if (removed == null || removed == 0L) {
            return;
        }

        String[] parts = member.split(":", 2);
        if (parts.length != 2) {
            return;
        }
        Long userId = Long.valueOf(parts[0]);
        String chatId = parts[1];

        chatHistoryMapper.delete(new LambdaQueryWrapper<ChatHistory>()
                .eq(ChatHistory::getChatId, chatId)
                .eq(ChatHistory::getUserId, userId));

        String conversationId = userId + ":" + chatId;
        chatMessageMapper.delete(new LambdaQueryWrapper<ChatMessage>()
                .eq(ChatMessage::getConversationId, conversationId));

        log.info("对话清理完成：chatId={}, userId={}", chatId, userId);
    }
}