package com.example.movie.common.memory;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.movie.entity.ChatMessage;
import com.example.movie.mapper.ChatMessageMapper;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 作者: 枷
 * 时间:2026/10/5 上午12:33
 * 描述:。。。
 */
@Component
public class MysqlChatMemory implements ChatMemory {

    private static final String CLEANUP_ZSET = "chat:cleanup";
    private static final long CLEANUP_DELAY_MS = 3 * 60 * 1000;
    /** 每次请求最多携带的历史消息条数，防止对话历史无限膨胀拖慢模型首字响应 */
    private static final int MAX_HISTORY_MESSAGES = 20;

    @Autowired
    private ChatMessageMapper chatMessageMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 向指定对话中添加一条消息
     * @param conversationId 对话ID
     * @param message 要添加的消息对象
     */
    /**
     * 从 conversationId 解析 userId。
     * 约定格式：{userId}:{chatId}；若无冒号则返回 null（未登录场景）。
     * 这样设计是为了在 reactor 流式线程上也能拿到 userId（ThreadLocal 不可跨线程）。
     */
    private Long parseUserId(String conversationId) {
        if (conversationId == null) return null;
        int idx = conversationId.indexOf(':');
        if (idx <= 0) return null;
        try {
            return Long.parseLong(conversationId.substring(0, idx));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public void add(String conversationId, Message message) {
        ChatMessage chatMessage = ChatMessage.builder()
                .conversationId(conversationId)
                .role(message.getMessageType().name())
                .context(message.getText())
                .userId(parseUserId(conversationId))
                .build();
        chatMessageMapper.insert(chatMessage);

        redisTemplate.opsForZSet().add(CLEANUP_ZSET, conversationId, System.currentTimeMillis() + CLEANUP_DELAY_MS);
    }

    /**
     * 向指定对话中添加多条消息
     * @param conversationId 对话ID
     * @param messages 要添加的消息列表
     */
    @Override
    public void add(String conversationId, List<Message> messages) {
        // 遍历消息列表，对每条消息调用add方法
        messages.forEach(message -> add(conversationId, message));
    }

    /**
     * 获取指定对话的所有消息
     * @param conversationId 对话ID
     * @return 该对话的所有消息列表
     */
    @Override
    public List<Message> get(String conversationId) {
        // conversationId 已包含 userId（格式 userId:chatId），按 conversationId 查询即完成用户隔离
        LambdaQueryWrapper<ChatMessage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ChatMessage::getConversationId, conversationId);

        // 只取最近 MAX_HISTORY_MESSAGES 条历史，避免对话历史无限膨胀拖慢模型首字响应
        queryWrapper.orderByDesc(ChatMessage::getId).last("LIMIT " + MAX_HISTORY_MESSAGES);
        // 查询数据库并将结果按时间正序转为Message列表（查询为倒序，需反转）
        List<ChatMessage> latest = chatMessageMapper.selectList(queryWrapper);
        java.util.Collections.reverse(latest);
        return latest.stream()
                .map(e -> switch (e.getRole()) {
                    case "USER"     -> new UserMessage(e.getContext());
                    case "ASSISTANT"-> new AssistantMessage(e.getContext());
                    case "SYSTEM"   -> new SystemMessage(e.getContext());
                    default         -> new UserMessage(e.getContext());
                })
                .collect(Collectors.toList());
    }

    /**
     * 清除指定对话的所有消息
     * @param conversationId 对话ID
     */
    @Override
    public void clear(String conversationId) {
        // conversationId 已包含 userId，按 conversationId 删除即完成用户隔离
        LambdaQueryWrapper<ChatMessage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ChatMessage::getConversationId, conversationId);
        // 删除符合条件的所有记录
        chatMessageMapper.delete(queryWrapper);
    }
}