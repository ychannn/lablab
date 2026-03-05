package org.ychan.lablab.dto.req;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * 管理员密码重置请求
 */
@Data
public class AdminResetPasswordReqDTO {

    @NotBlank(message = "旧密码不能为空")
    private String oldPassword;

    @NotBlank(message = "新密码不能为空")
    private String newPassword;
}