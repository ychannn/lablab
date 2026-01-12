package org.ychan.lablab.common.result;

import lombok.Data;

@Data
public class Result<T> {
    /**
     * 响应状态码：
     * 200 - 成功
     * 400 - 参数错误/请求错误
     * 404 - 资源不存在
     * 500 - 系统内部错误
     */
    private int code;

    private String message;

    private T data;

    private Result(int code, String message, T data){
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public Result(){

    }

    public static <T> Result<T> success(T data){
        return new Result<>(200, "操作成功", data);
    }

    public static <T> Result<T> success(){
        return new Result<>(200, "操作成功", null);
    }

    public static <T> Result<T> error(){
        return new Result<>(500, "服务器内部错误", null);
    }

    /**
     * 通用错误
     * @param message
     * @return
     * @param <T>
     */
    public static <T> Result<T> error(String message){
        return new Result<>(500, message, null);
    }
}
