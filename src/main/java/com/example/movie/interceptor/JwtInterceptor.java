package com.example.movie.interceptor;

import com.example.movie.Utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@Slf4j
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();
        log.info("====进入拦截器，uri：{}", uri);


        // 放行登录 注册 上传等公共接口
        if (uri.contains("/login") || uri.contains("/register") || uri.contains("/upload")) {
            return true;
        }

        // 放行的都是数据 输入网址并不会跳到对应页面
        if (uri.startsWith("/plus") ||
                uri.startsWith("/session/seats") || uri.startsWith("/hall/plus") ||
                uri.equals("/hall/counts") ||
                uri.startsWith("/movie/")) {
            return true;
        }

        // 以下接口需要登录，执行 token 校验
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write("{\"code\":401,\"msg\":\"未登录，请先登录\"}");
            return false;
        }
        String token = header.substring(7);
        if (!jwtUtil.validateToken(token)) {
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write("{\"code\":401,\"msg\":\"登录失效，请重新登录\"}");
            return false;
        }

        // 解析用户id、角色（0=管理员，1=普通用户）
        Long userId = jwtUtil.getUserId(token);
        Integer role = jwtUtil.getRole(token);
        request.setAttribute("currentUserId", userId);
        request.setAttribute("currentRole", role);

        // 用户端订单接口特殊处理
        if (uri.equals("/order/create") || uri.equals("/order/pay")) {
            if (role == null || role != 1) {
                response.setContentType("application/json;charset=utf-8");
                response.setStatus(403);
                response.getWriter().write("{\"code\":403,\"msg\":\"权限不足：仅普通用户可访问用户端页面\"}");
                return false;
            }
            return true;
        }

        // 通用角色判断：/users 和 /ai 均为普通用户（role=1）可访问
        if (uri.startsWith("/users") || uri.startsWith("/ai")) {
            if (role == null || role != 1) {
                response.setContentType("application/json;charset=utf-8");
                response.setStatus(403);
                response.getWriter().write("{\"code\":403,\"msg\":\"权限不足：仅普通用户可访问用户端页面\"}");
                return false;
            }
        } else {
            if (role == null || role != 0) {
                response.setContentType("application/json;charset=utf-8");
                response.setStatus(403);
                response.getWriter().write("{\"code\":403,\"msg\":\"权限不足：仅管理员可访问管理后台\"}");
                return false;
            }
        }

        return true;
    }
}