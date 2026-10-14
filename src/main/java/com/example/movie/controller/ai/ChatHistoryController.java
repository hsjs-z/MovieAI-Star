package com.example.movie.controller.ai;

import com.example.movie.Utils.JwtUtil;
import com.example.movie.common.UserContext;
import com.example.movie.common.memory.MysqlChatMemory;
import com.example.movie.repository.ChatHistoryRepository;
import com.example.movie.vo.MessageVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.Message;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/ai/history")
@RequiredArgsConstructor
public class ChatHistoryController {
    private final ChatHistoryRepository chatHistoryRepository;
    private final MysqlChatMemory mysqlChatMemory;
    private final JwtUtil jwtUtil;

    /**
     * 从 Authorization 头解析 userId 并写入 UserContext
     * @return userId，未登录返回 null
     */
    private Long resolveUserId(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (jwtUtil.validateToken(token)) {
                Long userId = jwtUtil.getUserId(token);
                UserContext.set(userId);
                return userId;
            }
        }
        return null;
    }

    @GetMapping("/{type}")
    public List<String> getChatIds(@PathVariable("type") String type,
                                   @RequestHeader(value = "Authorization", required = false) String authHeader) {
        Long userId = resolveUserId(authHeader);
        if (userId == null) {
            return Collections.emptyList();
        }
        return chatHistoryRepository.getChatIds(type, userId);
    }

    @GetMapping("/{type}/{chatId}")
    public List<MessageVO> getChatHistory(@PathVariable("type") String type,
                                          @PathVariable("chatId") String chatId,
                                          @RequestHeader(value = "Authorization", required = false) String authHeader) {
        Long userId = resolveUserId(authHeader);
        if (userId == null) {
            return Collections.emptyList();
        }
        try {
            // conversationId 格式为 userId:chatId，与 MovieServicelControlle 保持一致
            String conversationId = userId + ":" + chatId;
            List<Message> messages = mysqlChatMemory.get(conversationId);
            return messages.stream().map(MessageVO::new).toList();
        } finally {
            UserContext.remove();
        }
    }
}