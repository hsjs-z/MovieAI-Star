package com.example.movie.repository;

import java.util.List;

/**
 * 作者: 枷
 * 时间:2026/10/4 下午8:04
 * 描述:。。。
 */
public interface ChatHistoryRepository {
    void save(String type, String chatId, Long userId);

    /**
     * 获取会话ID列表
     * @param type 会话类型，如:chat、service、pdf
     * @param userId 用户ID，仅返回该用户的会话
     * @return 会话ID列表
     */
    List<String> getChatIds(String type, Long userId);
}
