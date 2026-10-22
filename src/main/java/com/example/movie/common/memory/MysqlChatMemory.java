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
@Component  // 将此类标记为Spring组件，由Spring容器管理
public class MysqlChatMemory implements ChatMemory {  // 实现ChatMemory接口，提供聊天消息存储功能

    private static final String CLEANUP_ZSET = "chat:cleanup";  // Redis中用于存储待清理对话的有序集合键名
    private static final long CLEANUP_DELAY_MS = 3 * 60 * 1000;  // 对话清理的延迟时间（3分钟）
    /** 每次请求最多携带的历史消息条数，防止对话历史无限膨胀拖慢模型首字响应 */
    private static final int MAX_HISTORY_MESSAGES = 20;  // 最大历史消息数量限制

    @Autowired  // 自动注入ChatMessageMapper，用于数据库操作
    private ChatMessageMapper chatMessageMapper;

    @Autowired  // 自动注入StringRedisTemplate，用于Redis操作
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
    private Long parseUserId(String conversationId) {  // 从对话ID中解析用户ID
        if (conversationId == null) return null;  // 如果对话ID为空，返回null
        int idx = conversationId.indexOf(':');  // 查找冒号位置
        if (idx <= 0) return null;  // 如果没有冒号或冒号在第一位，返回null
        try {
            return Long.parseLong(conversationId.substring(0, idx));  // 解析冒号前的部分为用户ID
        } catch (NumberFormatException e) {
            return null;  // 如果解析失败，返回null
        }
    }

    @Override  // 重写接口方法
    public void add(String conversationId, Message message) {  // 添加单条消息到对话
        ChatMessage chatMessage = ChatMessage.builder()  // 构建聊天消息对象
                .conversationId(conversationId)  // 设置对话ID
                .role(message.getMessageType().name())  // 设置消息角色
                .context(message.getText())  // 设置消息内容
                .userId(parseUserId(conversationId))  // 设置用户ID
                .build();
        chatMessageMapper.insert(chatMessage);  // 将消息插入数据库

        // 在Redis中设置对话的清理时间
        redisTemplate.opsForZSet().add(CLEANUP_ZSET, conversationId, System.currentTimeMillis() + CLEANUP_DELAY_MS);
    }

    /**
     * 向指定对话中添加多条消息
     * @param conversationId 对话ID
     * @param messages 要添加的消息列表
     */
    @Override  // 重写接口方法
    public void add(String conversationId, List<Message> messages) {  // 添加多条消息到对话
        // 遍历消息列表，对每条消息调用add方法
        messages.forEach(message -> add(conversationId, message));
    }

    /**
     * 获取指定对话的所有消息
     * @param conversationId 对话ID
     * @return 该对话的所有消息列表
     */
    @Override  // 重写接口方法
    public List<Message> get(String conversationId) {  // 获取对话的所有消息
        // conversationId 已包含 userId（格式 userId:chatId），按 conversationId 查询即完成用户隔离
        LambdaQueryWrapper<ChatMessage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ChatMessage::getConversationId, conversationId);

        // 只取最近 MAX_HISTORY_MESSAGES 条历史，避免对话历史无限膨胀拖慢模型首字响应
        queryWrapper.orderByDesc(ChatMessage::getId).last("LIMIT " + MAX_HISTORY_MESSAGES);
        // 查询数据库并将结果按时间正序转为Message列表（查询为倒序，需反转）
        List<ChatMessage> latest = chatMessageMapper.selectList(queryWrapper);
        java.util.Collections.reverse(latest);  // 反转列表，使消息按时间正序排列
        return latest.stream()
                .map(e -> switch (e.getRole()) {  // 根据角色类型创建不同的消息对象
                    case "USER"     -> new UserMessage(e.getContext());  // 用户消息
                    case "ASSISTANT"-> new AssistantMessage(e.getContext());  // 助手消息
                    case "SYSTEM"   -> new SystemMessage(e.getContext());  // 系统消息
                    default         -> new UserMessage(e.getContext());  // 默认作为用户消息
                })
                .collect(Collectors.toList());  // 将结果收集为列表
    }

    /**
     * 清除指定对话的所有消息
     * @param conversationId 对话ID
     */
    @Override  // 重写接口方法
    public void clear(String conversationId) {  // 清除对话的所有消息
        // conversationId 已包含 userId，按 conversationId 删除即完成用户隔离
        LambdaQueryWrapper<ChatMessage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ChatMessage::getConversationId, conversationId);
        // 删除符合条件的所有记录
        chatMessageMapper.delete(queryWrapper);
    }
}