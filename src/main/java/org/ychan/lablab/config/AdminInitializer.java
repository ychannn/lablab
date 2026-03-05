package org.ychan.lablab.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.ychan.lablab.common.constant.CommonConstants;
import org.ychan.lablab.entity.admin.Admin;
import org.ychan.lablab.mapper.AdminMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 管理员初始化配置
 */
@Configuration
@RequiredArgsConstructor
public class AdminInitializer {

    private final AdminMapper adminMapper;
    private static final BCryptPasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

    @Bean
    public ApplicationRunner initAdmin() {
        return args -> {
            // 检查是否已存在超级管理员账号
            Admin admin = adminMapper.selectOne(
                    new LambdaQueryWrapper<Admin>()
                            .eq(Admin::getUsername, "admin")
                            .eq(Admin::getDeleted, CommonConstants.FALSE));

            if (admin == null) {
                // 创建默认超级管理员账号
                Admin newAdmin = new Admin();
                newAdmin.setUsername("admin");
                newAdmin.setPassword(PASSWORD_ENCODER.encode("admin123"));
                newAdmin.setRole("admin");
                adminMapper.insert(newAdmin);
                System.out.println("默认超级管理员账号已创建：用户名=admin，密码=admin123");
            }
        };
    }
}