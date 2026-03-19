package org.ychan.lablab.dto.req;

import lombok.Data;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * 发送绑定/换绑邮箱验证码请求
 */
@Data
public class SendBindEmailCodeReqDTO {

    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;
}
