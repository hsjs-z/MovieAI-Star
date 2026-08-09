package com.example.movie.exception;

import com.example.movie.common.result.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 作者: 枷
 * 时间:2026/8/2 下午3:33
 * 描述:全局异常处理器
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    //处理异常
    // 1. 专门捕获 RuntimeException（你手动throw的业务提示）
    @ExceptionHandler(RuntimeException.class)
    public Result<?> handleRuntimeException(RuntimeException e) {
        e.printStackTrace();
        return Result.error(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public Result<?> handleAllException(Exception e) {
        e.printStackTrace();
        return Result.error("对不起,操作失败,请联系管理员");
    }
}
