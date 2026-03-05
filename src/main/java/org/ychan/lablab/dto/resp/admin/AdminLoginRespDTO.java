package org.ychan.lablab.dto.resp.admin;

import lombok.Data;

/**
 * 管理员登录成功返回
 */
@Data
public class AdminLoginRespDTO {

    /**
     * 登录凭证，请求需登录的接口时在 Header 中携带：Authorization: Bearer {token}
     */
    private String token;

    /**
     * 管理员 id
     */
    private Integer adminId;

    /**
     * 用户名
     */
    private String username;
}
