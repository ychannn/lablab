package org.ychan.lablab.service;

/**
 * 邮件发送（验证码等）
 * 未配置 SMTP 时实现类仅将验证码打印到控制台，便于本地调试
 */
public interface EmailService {

    /**
     * 发送验证码邮件到指定邮箱
     *
     * @param to   收件人邮箱
     * @param code 验证码
     */
    void sendVerificationCode(String to, String code);
}
