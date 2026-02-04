package com.fei.examsys.exception;

import com.fei.examsys.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author fei
 * description: 全局异常处理类
 */
@Slf4j
@RestControllerAdvice  // LEARN 代表处理全局异常
public class GlobalExceptionHandler {

    // 定义异常处理的handler
    @ExceptionHandler(Exception.class)
    public Result exceptionHandler(Exception e) {
        // 打印日志
        e.printStackTrace();
        log.error("代码出现异常, 异常信息为:{}", e.getMessage());
        return Result.error(e.getMessage());
    }
}
