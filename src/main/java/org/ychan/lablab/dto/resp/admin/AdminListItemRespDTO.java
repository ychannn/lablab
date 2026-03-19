package org.ychan.lablab.dto.resp.admin;

import lombok.Data;

/**
 * 管理员列表项（不含密码）
 */
@Data
public class AdminListItemRespDTO {

    private Integer id;
    private String username;
    private String role;
    private String email;
}
