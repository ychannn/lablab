package org.ychan.lablab.handler;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.ychan.lablab.common.result.Result;
import org.ychan.lablab.eception.BusinessException;

import java.util.stream.Collectors;

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
     * 处理 @Valid 校验失败（如 NotBlank）
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> handleValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining("; "));
        log.warn("参数校验失败, 请求地址{}, 信息：{}", request.getRequestURL(), message);
        return Result.error(message);
    }

    @ExceptionHandler(BindException.class)
    public Result<?> handleBindException(BindException e, HttpServletRequest request) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining("; "));
        log.warn("参数绑定失败, 请求地址{}, 信息：{}", request.getRequestURL(), message);
        return Result.error(message);
    }

    /**
     * 处理所有未预期异常：仅记录完整堆栈到日志，对前端统一返回友好提示，不暴露异常栈与内部信息
     */
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e, HttpServletRequest request) {
        log.error("请求异常, 地址: {}", request.getRequestURI(), e);
        return Result.error("操作失败，请稍后重试");
    }
}
