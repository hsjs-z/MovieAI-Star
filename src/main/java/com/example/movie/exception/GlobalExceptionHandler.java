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
    @ExceptionHandler
    public Result ex(Exception e){//方法形参中指定能够处理的异常类型
        e.printStackTrace();//打印堆栈中的异常信息
        //捕获到异常之后，响应一个标准的Result
        return Result.error("对不起,操作失败,请联系管理员");
    }
}
