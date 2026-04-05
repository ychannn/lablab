package org.ychan.lablab.service;

/**
 * 邮件服务。
 * 未配置 SMTP 时实现类仅将验证码打印到控制台，便于本地调试
 */
public interface EmailService {

    /**
     * 发送验证码邮件
     *
     * @param to   目标邮箱
     * @param code 验证码
     * @return 是否真正通过SMTP发送成功
     */
    boolean sendVerificationCode(String to, String code);

}
