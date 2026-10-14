package com.example.movie.controller.ai;

import com.example.movie.Utils.JwtUtil;
import com.example.movie.repository.ChatHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.Map;

/**
 * 作者: 枷
 * 时间:2026/10/6 上午1:51
 * 描述:AI客服主控制器，流式返回。从Authorization头解析token，通过ToolContext把userId传给Tool层（流式执行在线程池线程上，ThreadLocal不可用）
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/ai")
public class MovieServicelControlle {

    private final ChatClient serviceChatClient;
    private final ChatHistoryRepository recordService;
    private final JwtUtil jwtUtil;

    @RequestMapping(value = "/service", produces = "text/html;charset=utf-8")
    public Flux<String> service(String prompt, String chatId,
                                @RequestHeader(value = "Authorization", required = false) String authHeader) {
        // 从 token 解析当前用户ID
        Long userId = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (jwtUtil.validateToken(token)) {
                userId = jwtUtil.getUserId(token);
                log.debug("AI客服请求，userId={}", userId);
            }
        }

        String id = (chatId == null || chatId.isBlank()) ? "default" : chatId;
        // 仅在已登录时记录会话归属；未登录不持久化会话列表
        if (userId != null) {
            recordService.save("service", id, userId);
        }

        String conversationId = userId != null ? userId + ":" + id : id;

        ChatClient.ChatClientRequestSpec request = serviceChatClient.prompt()
                .user(prompt)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId));
        // 流式订阅在线程池线程上执行，ThreadLocal 无法跨线程，userId 必须通过 ToolContext 传给工具
        if (userId != null) {
            request = request.toolContext(Map.of("userId", userId));
        }
        return request.stream()
                .content()
                .doOnError(e -> log.error("AI客服流式响应失败, chatId={}", id, e));
    }
}