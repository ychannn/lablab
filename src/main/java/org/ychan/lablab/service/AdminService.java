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
     * 重置密码
     */
    void resetPassword(String token, String oldPassword, String newPassword);

    /**
     * 刷新 token
     */
    AdminLoginRespDTO refreshToken(String token);
}
