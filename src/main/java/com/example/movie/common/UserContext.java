package com.example.movie.common;

/**
 * 作者: 枷
 * 时间:2026/10/6
 * 描述:当前登录用户上下文，ThreadLocal实现。ChatController解析token后set，Tool里get
 */
public class UserContext {

    private static final ThreadLocal<Long> CURRENT_USER_ID = new ThreadLocal<>();

    public static void set(Long userId) {
        CURRENT_USER_ID.set(userId);
    }

    public static Long get() {
        return CURRENT_USER_ID.get();
    }

    public static void remove() {
        CURRENT_USER_ID.remove();
    }
}