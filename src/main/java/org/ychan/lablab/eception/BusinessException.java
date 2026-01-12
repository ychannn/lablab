package org.ychan.lablab.eception;

/**
 * 业务异常
 */
public class BusinessException extends RuntimeException{
    /**
     * 异常信息
     */
    String message;

    public BusinessException(String message){
        super(message);
        this.message = message;
    }

    @Override
    public String getMessage() {
            return message;
    }
}
