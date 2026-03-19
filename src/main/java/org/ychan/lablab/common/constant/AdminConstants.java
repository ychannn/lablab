package org.ychan.lablab.common.constant;

/**
 * 管理员相关常量
 */
public final class AdminConstants {

    /** Redis 中 token 的 key 前缀，完整 key: admin:token:{token} */
    public static final String REDIS_TOKEN_PREFIX = "admin:token:";
    /** Token 过期时间（秒），默认 2 小时 */
    public static final long TOKEN_EXPIRE_SECONDS = 2 * 60 * 60;

    /** 邮箱验证码 Redis key 前缀，完整 key: admin:email:code:{email}，value: adminId:code */
    public static final String REDIS_EMAIL_CODE_PREFIX = "admin:email:code:";
    /** 邮箱验证码有效期（秒），默认 10 分钟 */
    public static final long EMAIL_CODE_EXPIRE_SECONDS = 10 * 60;

    private AdminConstants() {}
}
