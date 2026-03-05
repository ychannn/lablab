package org.ychan.lablab.common.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.ychan.lablab.common.constant.AdminConstants;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

/**
 * JWT 工具类
 */
public class JwtUtils {

    /**
     * 生成 JWT token
     */
    public static String generateToken(Map<String, Object> claims) {
        SecretKey key = Keys.hmacShaKeyFor("your-secret-key-for-jwt-token-generation".getBytes());
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + AdminConstants.TOKEN_EXPIRE_SECONDS * 1000);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expireDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 解析 JWT token
     */
    public static Claims parseToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor("your-secret-key-for-jwt-token-generation".getBytes());
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 从 token 中获取管理员 ID
     */
    public static Integer getAdminIdFromToken(String token) {
        Claims claims = parseToken(token);
        return (Integer) claims.get("adminId");
    }

    /**
     * 从 token 中获取用户名
     */
    public static String getUsernameFromToken(String token) {
        Claims claims = parseToken(token);
        return (String) claims.get("username");
    }

    /**
     * 检查 token 是否过期
     */
    public static boolean isTokenExpired(String token) {
        Claims claims = parseToken(token);
        return claims.getExpiration().before(new Date());
    }
}