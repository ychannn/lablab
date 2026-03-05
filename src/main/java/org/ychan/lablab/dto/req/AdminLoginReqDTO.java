package org.ychan.lablab.dto.req;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * 管理员登录请求
 */
@Data
public class AdminLoginReqDTO {

    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;
}
