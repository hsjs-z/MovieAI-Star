package com.example.movie.interceptor;

import com.example.movie.Utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 作者: 枷
 * 时间:2026/8/3 上午10:41
 * 描述:拦截器，校验token合法性
 */ //16 19
//@Component
public class JwtInterceptor implements HandlerInterceptor {

//    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 拦截器，校验token合法性
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 放行登录、注册接口，不需要token
        String uri = request.getRequestURI();
        if (uri.contains("/login") || uri.contains("/register")) {
            return true;
        }

        // 获取请求头token
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write("{\"code\":401,\"msg\":\"未登录，请先登录\"}");
            return false;
        }
        String token = header.substring(7);

        // 校验token合法性
        if (!jwtUtil.validateToken(token)) {
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write("{\"code\":401,\"msg\":\"登录失效，请重新登录\"}");
            return false;
        }

        // 解析用户id存入request，接口可直接获取当前登录用户
        Long userId = jwtUtil.getUserId(token);
        request.setAttribute("currentUserId", userId);
        return true;
    }
}
