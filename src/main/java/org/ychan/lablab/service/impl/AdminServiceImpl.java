package org.ychan.lablab.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.ychan.lablab.common.constant.AdminConstants;
import org.ychan.lablab.common.constant.CommonConstants;

import java.time.Duration;
import org.ychan.lablab.common.utils.JwtUtils;
import org.ychan.lablab.dto.req.AdminLoginReqDTO;
import org.ychan.lablab.dto.resp.admin.AdminLoginRespDTO;
import org.ychan.lablab.eception.BusinessException;
import org.ychan.lablab.entity.admin.Admin;
import org.ychan.lablab.mapper.AdminMapper;
import org.ychan.lablab.service.AdminService;
import org.ychan.lablab.service.EmailService;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final AdminMapper adminMapper;
    private final RedissonClient redissonClient;
    private final EmailService emailService;

    private static final BCryptPasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

    @Override
    public AdminLoginRespDTO login(AdminLoginReqDTO req) {
        Admin admin = adminMapper.selectOne(
                new LambdaQueryWrapper<Admin>()
                        .eq(Admin::getUsername, req.getUsername())
                        .eq(Admin::getDeleted, CommonConstants.FALSE));
        if (admin == null) {
            throw new BusinessException("用户名或密码错误");
        }
        if (!PASSWORD_ENCODER.matches(req.getPassword(), admin.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        // 生成 JWT token
        Map<String, Object> claims = new HashMap<>();
        claims.put("adminId", admin.getId());
        claims.put("username", admin.getUsername());
        claims.put("role", admin.getRole());
        String token = JwtUtils.generateToken(claims);

        // 将 token 存储到 Redis，用于登出功能
        String redisKey = AdminConstants.REDIS_TOKEN_PREFIX + token;
        RBucket<Integer> bucket = redissonClient.getBucket(redisKey);
        bucket.set(admin.getId(), java.time.Duration.ofSeconds(AdminConstants.TOKEN_EXPIRE_SECONDS));

        AdminLoginRespDTO resp = new AdminLoginRespDTO();
        resp.setToken(token);
        resp.setAdminId(admin.getId());
        resp.setUsername(admin.getUsername());
        return resp;
    }

    @Override
    public void logout(String token) {
        if (token == null || token.isBlank()) {
            return;
        }
        String redisKey = AdminConstants.REDIS_TOKEN_PREFIX + token.trim();
        redissonClient.getBucket(redisKey).delete();
    }

    @Override
    public boolean validateToken(String token) {
        if (token == null || token.isBlank()) {
            return false;
        }

        try {
            // 检查 JWT token 是否过期
            if (JwtUtils.isTokenExpired(token)) {
                // 如果过期，从 Redis 中删除
                String redisKey = AdminConstants.REDIS_TOKEN_PREFIX + token.trim();
                redissonClient.getBucket(redisKey).delete();
                return false;
            }

            // 检查 token 是否在 Redis 中（用于登出功能）
            String redisKey = AdminConstants.REDIS_TOKEN_PREFIX + token.trim();
            return redissonClient.getBucket(redisKey).isExists();
        } catch (Exception e) {
            // token 解析失败，返回 false
            return false;
        }
    }

    @Override
    public Admin getAdminByToken(String token) {
        if (token == null || token.isBlank()) {
            return null;
        }

        try {
            // 从 JWT token 中获取管理员 ID
            Integer adminId = JwtUtils.getAdminIdFromToken(token);
            if (adminId == null) {
                return null;
            }

            // 检查 token 是否在 Redis 中（用于登出功能）
            String redisKey = AdminConstants.REDIS_TOKEN_PREFIX + token.trim();
            if (!redissonClient.getBucket(redisKey).isExists()) {
                return null;
            }

            return adminMapper.selectById(adminId);
        } catch (Exception e) {
            // token 解析失败，返回 null
            return null;
        }
    }

    @Override
    public void resetPassword(String token, String oldPassword, String newPassword) {
        Admin admin = getAdminByToken(token);
        if (admin == null) {
            throw new BusinessException("登录已过期，请重新登录");
        }

        if (!PASSWORD_ENCODER.matches(oldPassword, admin.getPassword())) {
            throw new BusinessException("旧密码错误");
        }

        admin.setPassword(PASSWORD_ENCODER.encode(newPassword));
        adminMapper.updateById(admin);
    }

    @Override
    public AdminLoginRespDTO refreshToken(String token) {
        Admin admin = getAdminByToken(token);
        if (admin == null) {
            throw new BusinessException("登录已过期，请重新登录");
        }

        // 生成新的 JWT token
        Map<String, Object> claims = new HashMap<>();
        claims.put("adminId", admin.getId());
        claims.put("username", admin.getUsername());
        claims.put("role", admin.getRole());
        String newToken = JwtUtils.generateToken(claims);

        // 删除旧 token
        String oldRedisKey = AdminConstants.REDIS_TOKEN_PREFIX + token.trim();
        redissonClient.getBucket(oldRedisKey).delete();

        // 存储新 token
        String newRedisKey = AdminConstants.REDIS_TOKEN_PREFIX + newToken;
        RBucket<Integer> bucket = redissonClient.getBucket(newRedisKey);
        bucket.set(admin.getId(), java.time.Duration.ofSeconds(AdminConstants.TOKEN_EXPIRE_SECONDS));

        AdminLoginRespDTO resp = new AdminLoginRespDTO();
        resp.setToken(newToken);
        resp.setAdminId(admin.getId());
        resp.setUsername(admin.getUsername());
        return resp;
    }

    @Override
    public void addAdmin(String token, String username, String password, String role) {
        // 验证当前登录的用户是否是超级管理员
        Admin currentAdmin = getAdminByToken(token);
        if (currentAdmin == null) {
            throw new BusinessException("登录已过期，请重新登录");
        }
        if (!"admin".equals(currentAdmin.getRole())) {
            throw new BusinessException("权限不足，只有超级管理员才能添加管理员");
        }

        // 检查用户名是否已存在
        Admin existingAdmin = adminMapper.selectOne(
                new LambdaQueryWrapper<Admin>()
                        .eq(Admin::getUsername, username)
                        .eq(Admin::getDeleted, CommonConstants.FALSE));
        if (existingAdmin != null) {
            throw new BusinessException("用户名已存在");
        }

        // 创建新管理员
        Admin admin = new Admin();
        admin.setUsername(username);
        admin.setPassword(PASSWORD_ENCODER.encode(password));
        admin.setRole(role);
        adminMapper.insert(admin);
    }

    @Override
    public void bindEmail(String token, String email) {
        Admin admin = getAdminByToken(token);
        if (admin == null) {
            throw new BusinessException("登录已过期，请重新登录");
        }
        admin.setEmail(email.trim());
        adminMapper.updateById(admin);
    }

    @Override
    public void sendEmailCode(String token) {
        Admin admin = getAdminByToken(token);
        if (admin == null) {
            throw new BusinessException("登录已过期，请重新登录");
        }
        String email = admin.getEmail();
        if (email == null || email.isBlank()) {
            throw new BusinessException("请先绑定邮箱");
        }
        email = email.trim();
        String code = String.format("%06d", ThreadLocalRandom.current().nextInt(1000000));
        String key = AdminConstants.REDIS_EMAIL_CODE_PREFIX + email.toLowerCase();
        String value = admin.getId() + ":" + code;
        RBucket<String> bucket = redissonClient.getBucket(key);
        bucket.set(value, Duration.ofSeconds(AdminConstants.EMAIL_CODE_EXPIRE_SECONDS));
        emailService.sendVerificationCode(email, code);
    }

    @Override
    public void changePasswordByEmail(String token, String code, String newPassword) {
        Admin admin = getAdminByToken(token);
        if (admin == null) {
            throw new BusinessException("登录已过期，请重新登录");
        }
        String email = admin.getEmail();
        if (email == null || email.isBlank()) {
            throw new BusinessException("请先绑定邮箱");
        }
        String key = AdminConstants.REDIS_EMAIL_CODE_PREFIX + email.trim().toLowerCase();
        RBucket<String> bucket = redissonClient.getBucket(key);
        String stored = bucket.get();
        if (stored == null) {
            throw new BusinessException("验证码已过期或未发送，请重新获取");
        }
        String[] parts = stored.split(":", 2);
        if (parts.length != 2 || !parts[0].equals(String.valueOf(admin.getId())) || !parts[1].equals(code.trim())) {
            throw new BusinessException("验证码错误");
        }
        bucket.delete();
        admin.setPassword(PASSWORD_ENCODER.encode(newPassword));
        adminMapper.updateById(admin);
    }
}
