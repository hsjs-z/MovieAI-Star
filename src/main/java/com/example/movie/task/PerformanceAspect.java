package com.example.movie.task;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 作者: 枷
 * 时间:2026/8/18 上午10:33
 * 描述:性能接口
 */
@Aspect
@Component
@Slf4j
public class PerformanceAspect {
    @Around("execution(* com.example.movie.controller..*.*(..))()")

    public Object measureExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object result;
        try {
            // 执行目标方法
            result = joinPoint.proceed();
            return result;
        } finally {
            long end = System.currentTimeMillis();
            long duration = end - start;

            // 获取当前请求信息
            HttpServletRequest request = getRequest();
            String uri = request != null ? request.getRequestURI() : "unknown";
            String method = joinPoint.getSignature().toShortString();

            // 输出性能日志
            log.info("接口性能：{} | {} | 耗时: {} ms", uri, method, duration);

            // 慢接口警告（超过1秒打印警告）
            if (duration > 1000) {
                log.warn("慢接口警告：{} 耗时 {} ms", uri, duration);
            }
        }
    }

    /**
     * 获取当前请求对象
     */
    private HttpServletRequest getRequest() {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }
}
