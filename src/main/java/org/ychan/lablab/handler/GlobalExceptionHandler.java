package org.ychan.lablab.handler;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.ychan.lablab.common.result.Result;
import org.ychan.lablab.eception.BusinessException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务异常
     * @param e
     * @param request
     * @return
     */
    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusinessException(BusinessException e, HttpServletRequest request){
        log.error("业务异常, 请求地址{}, 异常信息：{}", request.getRequestURL(), e.getMessage());
        return Result.error(e.getMessage());
    }

    /**
     * 处理所有异常
     * @param e
     * @param request
     * @return
     */
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e, HttpServletRequest request){
        log.error("业务异常, 请求地址{}, 异常信息:{}", request.getRequestURL(), e.getMessage());
        return Result.error(e.getMessage());
    }
}
