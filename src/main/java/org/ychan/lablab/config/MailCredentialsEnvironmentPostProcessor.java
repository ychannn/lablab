package org.ychan.lablab.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 从外部文件加载邮件账号与授权码，注入为 spring.mail.username / spring.mail.password，
 * 避免在 application.yml 中明文填写。文件路径由 app.mail-credentials-file 指定。
 */
public class MailCredentialsEnvironmentPostProcessor implements EnvironmentPostProcessor {

    private static final Logger log = LoggerFactory.getLogger(MailCredentialsEnvironmentPostProcessor.class);
    private static final String MAIL_CREDENTIALS_FILE_KEY = "app.mail-credentials-file";
    private static final String PROPERTY_SOURCE_NAME = "mailCredentialsFile";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        String pathStr = environment.getProperty(MAIL_CREDENTIALS_FILE_KEY);
        if (pathStr == null || pathStr.isBlank()) {
            return;
        }

        Path path = Paths.get(pathStr);
        if (!path.isAbsolute()) {
            path = Paths.get(System.getProperty("user.dir", ".")).resolve(path).normalize();
        }
        if (!Files.isRegularFile(path)) {
            log.warn("app.mail-credentials-file 已配置但文件不存在: {}，将不注入邮件账号/授权码", path);
            return;
        }

        Properties props = new Properties();
        try (var reader = Files.newBufferedReader(path)) {
            props.load(reader);
        } catch (IOException e) {
            throw new IllegalStateException("无法读取邮件凭证文件: " + path, e);
        }

        String username = props.getProperty("mail.username");
        String password = props.getProperty("mail.password");
        if (username == null && password == null) {
            return;
        }

        Map<String, Object> map = new HashMap<>();
        if (username != null) {
            map.put("spring.mail.username", username.trim());
        }
        if (password != null) {
            map.put("spring.mail.password", password.trim());
        }
        if (!map.isEmpty()) {
            environment.getPropertySources().addFirst(new MapPropertySource(PROPERTY_SOURCE_NAME, map));
        }
    }
}
