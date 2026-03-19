-- 管理员绑定邮箱（用于验证码修改密码），支持多管理员各自绑定
ALTER TABLE t_admin ADD COLUMN email VARCHAR(255) DEFAULT NULL COMMENT '绑定邮箱';
