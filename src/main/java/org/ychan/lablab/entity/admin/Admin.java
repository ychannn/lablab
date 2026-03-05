package org.ychan.lablab.entity.admin;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.ychan.lablab.entity.team.BaseEntity;

/**
 * 管理员
 */
@Data
@TableName("t_admin")
public class Admin extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 登录账号，唯一
     */
    private String username;

    /**
     * 密码（BCrypt 加密存储）
     */
    private String password;

    /**
     * 角色：admin（超级管理员）、operator（操作员）
     */
    private String role;
}
