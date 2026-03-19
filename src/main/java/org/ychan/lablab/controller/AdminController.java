package org.ychan.lablab.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.ychan.lablab.common.result.Result;
import org.ychan.lablab.config.RequiredRole;
import org.ychan.lablab.dto.req.AdminAddReqDTO;
import org.ychan.lablab.dto.req.AdminLoginReqDTO;
import org.ychan.lablab.dto.req.AdminResetPasswordReqDTO;
import org.ychan.lablab.dto.req.BindEmailReqDTO;
import org.ychan.lablab.dto.req.ChangePasswordByEmailReqDTO;
import org.ychan.lablab.dto.resp.admin.AdminLoginRespDTO;
import org.ychan.lablab.entity.admin.Admin;
import org.ychan.lablab.enums.RoleEnum;
import org.ychan.lablab.service.AdminService;

/**
 * 管理员登录等
 */
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    /**
     * 管理员登录
     * @param req username、password
     * @return token、adminId、username；后续请求在 Header 中携带 Authorization: Bearer {token}
     */
    @PostMapping("/login")
    public Result<AdminLoginRespDTO> login(@Valid @RequestBody AdminLoginReqDTO req) {
        return Result.success(adminService.login(req));
    }

    /**
     * 登出，使当前 token 失效
     * @param authorization Header 中的 Authorization: Bearer {token}，或直接传 token
     */
    @PostMapping("/logout")
    public Result<Void> logout(@RequestHeader(value = "Authorization", required = false) String authorization) {
        String token = parseToken(authorization);
        adminService.logout(token);
        return Result.success();
    }

    private static String parseToken(String authorization) {
        if (authorization == null || authorization.isBlank()) {
            return null;
        }
        String s = authorization.trim();
        if (s.startsWith("Bearer ")) {
            return s.substring(7).trim();
        }
        return s;
    }

    /**
     * 获取当前登录管理员信息
     */
    @GetMapping("/info")
    public Result<Admin> getCurrentAdmin(@RequestHeader(value = "Authorization", required = false) String authorization) {
        String token = parseToken(authorization);
        return Result.success(adminService.getAdminByToken(token));
    }

    /**
     * 重置密码（需旧密码）
     */
    @PostMapping("/reset-password")
    public Result<Void> resetPassword(@RequestHeader(value = "Authorization", required = false) String authorization, @Valid @RequestBody AdminResetPasswordReqDTO req) {
        String token = parseToken(authorization);
        adminService.resetPassword(token, req.getOldPassword(), req.getNewPassword());
        return Result.success();
    }

    /**
     * 绑定当前管理员的邮箱（用于接收验证码修改密码）
     */
    @PostMapping("/bind-email")
    public Result<Void> bindEmail(@RequestHeader(value = "Authorization", required = false) String authorization, @Valid @RequestBody BindEmailReqDTO req) {
        String token = parseToken(authorization);
        adminService.bindEmail(token, req.getEmail());
        return Result.success();
    }

    /**
     * 发送验证码到当前管理员的绑定邮箱（用于修改密码）
     */
    @PostMapping("/send-email-code")
    public Result<Void> sendEmailCode(@RequestHeader(value = "Authorization", required = false) String authorization) {
        String token = parseToken(authorization);
        adminService.sendEmailCode(token);
        return Result.success();
    }

    /**
     * 通过绑定邮箱的验证码修改密码
     */
    @PostMapping("/change-password-by-email")
    public Result<Void> changePasswordByEmail(@RequestHeader(value = "Authorization", required = false) String authorization, @Valid @RequestBody ChangePasswordByEmailReqDTO req) {
        String token = parseToken(authorization);
        adminService.changePasswordByEmail(token, req.getCode(), req.getNewPassword());
        return Result.success();
    }

    /**
     * 刷新 token
     */
    @PostMapping("/refresh-token")
    public Result<AdminLoginRespDTO> refreshToken(@RequestHeader(value = "Authorization", required = false) String authorization) {
        String token = parseToken(authorization);
        return Result.success(adminService.refreshToken(token));
    }

    /**
     * 添加管理员
     */
    @PostMapping("/add")
    @RequiredRole({RoleEnum.ADMIN})
    public Result<Void> addAdmin(@RequestHeader(value = "Authorization", required = false) String authorization, @Valid @RequestBody AdminAddReqDTO req) {
        String token = parseToken(authorization);
        adminService.addAdmin(token, req.getUsername(), req.getPassword(), req.getRole());
        return Result.success();
    }
}
