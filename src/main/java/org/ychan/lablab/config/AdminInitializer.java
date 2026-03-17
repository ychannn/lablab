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
            // 先查未删除的 admin
            Admin admin = adminMapper.selectOne(
                    new LambdaQueryWrapper<Admin>()
                            .eq(Admin::getUsername, "admin")
                            .eq(Admin::getDeleted, CommonConstants.FALSE));
            if (admin != null) {
                return; // 已有可用账号
            }
            // 再查是否存在（含已删除的），避免唯一约束冲突
            Admin anyAdmin = adminMapper.selectOne(
                    new LambdaQueryWrapper<Admin>()
                            .eq(Admin::getUsername, "admin"));
            if (anyAdmin != null) {
                // 恢复已删除的 admin 并重置为默认密码
                anyAdmin.setDeleted(CommonConstants.FALSE);
                anyAdmin.setPassword(PASSWORD_ENCODER.encode("admin123"));
                anyAdmin.setRole("admin");
                adminMapper.updateById(anyAdmin);
                System.out.println("默认超级管理员已恢复：用户名=admin，密码=admin123");
                return;
            }
            // 不存在则新建
            Admin newAdmin = new Admin();
            newAdmin.setUsername("admin");
            newAdmin.setPassword(PASSWORD_ENCODER.encode("admin123"));
            newAdmin.setRole("admin");
            adminMapper.insert(newAdmin);
            System.out.println("默认超级管理员账号已创建：用户名=admin，密码=admin123");
        };
    }
}