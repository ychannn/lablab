package org.ychan.lablab.service;

import org.ychan.lablab.dto.req.AdminLoginReqDTO;
import org.ychan.lablab.dto.resp.admin.AdminLoginRespDTO;
import org.ychan.lablab.entity.admin.Admin;

/**
 * 管理员登录等
 */
public interface AdminService {

    /**
     * 管理员登录：校验用户名密码，成功返回 token 及基本信息
     */
    AdminLoginRespDTO login(AdminLoginReqDTO req);

    /**
     * 登出：使当前 token 失效
     */
    void logout(String token);

    /**
     * 校验 token 是否有效
     */
    boolean validateToken(String token);

    /**
     * 根据 token 获取管理员信息
     */
    Admin getAdminByToken(String token);

    /**
     * 刷新 token
     */
    AdminLoginRespDTO refreshToken(String token);

    /**
     * 添加管理员
     */
    void addAdmin(String token, String username, String password, String role);

    /**
     * 发送绑定/换绑邮箱的验证码到指定邮箱（该邮箱将收到验证码，验证后即可绑定或换绑）
     */
    void sendBindEmailCode(String token, String email);

    /**
     * 绑定或换绑当前管理员的邮箱（需先获取验证码）
     */
    void bindEmail(String token, String email, String code);

    /**
     * 发送验证码到当前管理员的绑定邮箱（用于修改密码），验证码存入 Redis，有效期 10 分钟
     */
    void sendEmailCode(String token);

    /**
     * 通过绑定邮箱的验证码修改密码
     */
    void changePasswordByEmail(String token, String code, String newPassword);
}
