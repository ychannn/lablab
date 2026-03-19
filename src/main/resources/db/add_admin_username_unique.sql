-- 管理员用户名唯一，避免并发添加时重复（先查后插的竞态）
-- 若表已有重复 username，需先手工处理后再执行
-- 说明：软删除后该用户名仍占位，不可再插入同名新账号
ALTER TABLE t_admin ADD UNIQUE KEY uk_username (username);
