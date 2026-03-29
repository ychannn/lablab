package org.ychan.lablab.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.ychan.lablab.common.result.Result;
import org.ychan.lablab.config.RequiredRole;
import org.ychan.lablab.dto.req.AdminAddReqDTO;
import org.ychan.lablab.dto.req.AdminLoginReqDTO;
import org.ychan.lablab.dto.req.BindEmailReqDTO;
import org.ychan.lablab.dto.req.ChangePasswordByEmailReqDTO;
import org.ychan.lablab.dto.req.ForgotPasswordResetReqDTO;
import org.ychan.lablab.dto.req.ForgotPasswordSendCodeReqDTO;
import org.ychan.lablab.dto.req.SendBindEmailCodeReqDTO;
import org.ychan.lablab.dto.resp.admin.AdminListItemRespDTO;
import org.ychan.lablab.dto.resp.admin.AdminLoginRespDTO;
import org.ychan.lablab.entity.admin.Admin;
import org.ychan.lablab.enums.RoleEnum;
import org.ychan.lablab.service.AdminService;

import java.util.List;

/**
 * 管理员登录等
 */
@RestController
@RequestMapping("/api/admin")
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
     * 发送绑定/换绑邮箱的验证码到指定邮箱
     */
    @PostMapping("/send-bind-email-code")
    public Result<Void> sendBindEmailCode(@RequestHeader(value = "Authorization", required = false) String authorization, @Valid @RequestBody SendBindEmailCodeReqDTO req) {
        String token = parseToken(authorization);
        adminService.sendBindEmailCode(token, req.getEmail());
        return Result.success();
    }

    /**
     * 绑定或换绑当前管理员的邮箱（需先获取验证码）
     */
    @PostMapping("/bind-email")
    public Result<Void> bindEmail(@RequestHeader(value = "Authorization", required = false) String authorization, @Valid @RequestBody BindEmailReqDTO req) {
        String token = parseToken(authorization);
        adminService.bindEmail(token, req.getEmail(), req.getCode());
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
     * 添加管理员（仅超级管理员）
     */
    @PostMapping("/add")
    @RequiredRole({RoleEnum.ADMIN})
    public Result<Void> addAdmin(@RequestHeader(value = "Authorization", required = false) String authorization, @Valid @RequestBody AdminAddReqDTO req) {
        String token = parseToken(authorization);
        adminService.addAdmin(token, req.getUsername(), req.getPassword(), req.getRole());
        return Result.success();
    }

    /**
     * 管理员列表（仅超级管理员，不含密码）
     */
    @GetMapping("/list")
    @RequiredRole({RoleEnum.ADMIN})
    public Result<List<AdminListItemRespDTO>> listAdmins(@RequestHeader(value = "Authorization", required = false) String authorization) {
        String token = parseToken(authorization);
        return Result.success(adminService.listAdmins(token));
    }

    /**
     * 移除子管理员（仅超级管理员，不能移除自己或其它超级管理员）
     */
    @DeleteMapping("/{adminId}")
    @RequiredRole({RoleEnum.ADMIN})
    public Result<Void> removeAdmin(@RequestHeader(value = "Authorization", required = false) String authorization, @PathVariable Integer adminId) {
        String token = parseToken(authorization);
        adminService.removeAdmin(token, adminId);
        return Result.success();
    }

    /**
     * 忘记密码：发送验证码到绑定该邮箱的管理员（无需登录）
     */
    @PostMapping("/forgot-password/send-code")
    public Result<Void> sendForgotPasswordCode(@Valid @RequestBody ForgotPasswordSendCodeReqDTO req) {
        adminService.sendForgotPasswordCode(req.getEmail());
        return Result.success();
    }

    /**
     * 忘记密码：凭邮箱+验证码重置密码（无需登录）
     */
    @PostMapping("/forgot-password/reset")
    public Result<Void> resetPasswordByForgot(@Valid @RequestBody ForgotPasswordResetReqDTO req) {
        adminService.resetPasswordByForgotEmail(req.getEmail(), req.getCode(), req.getNewPassword());
        return Result.success();
    }
}
