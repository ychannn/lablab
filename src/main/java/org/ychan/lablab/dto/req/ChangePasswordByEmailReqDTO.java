package org.ychan.lablab.dto.req;

import lombok.Data;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 通过邮箱验证码修改密码请求
 */
@Data
public class ChangePasswordByEmailReqDTO {

    @NotBlank(message = "验证码不能为空")
    @Size(min = 4, max = 8, message = "验证码为4-8位")
    private String code;

    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, max = 32, message = "密码长度为6-32位")
    private String newPassword;
}
