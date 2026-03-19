package org.ychan.lablab.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.ychan.lablab.service.EmailService;

/**
 * 邮件发送实现。
 * 未配置 SMTP 时 JavaMailSender 为空，验证码仅打印到控制台（开发/模拟）。
 */
@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String from;

    @Override
    public void sendVerificationCode(String to, String code) {
        if (mailSender == null || from == null || from.isBlank()) {
            log.info("[邮件模拟] 验证码已“发送”到 {}，验证码：{}（未配置 SMTP，仅打印到控制台）", to, code);
            return;
        }
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(to);
            message.setSubject("【实验室后台】邮箱验证码");
            message.setText("您的验证码是：" + code + "，10分钟内有效。如非本人操作请忽略。");
            mailSender.send(message);
            log.info("[SMTP 已发] 验证码邮件已发送至 {}（发件人：{}）", to, from);
        } catch (Exception e) {
            log.warn("发送验证码邮件失败，改为打印到控制台。to={}, code={}, 错误: {}", to, code, e.getMessage(), e);
            log.info("[邮件模拟] 验证码：{}（原因：{}）", code, e.getMessage());
        }
    }
}
