package com.example.movie.Utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;

/**
 * 作者: 枷
 * 时间:2026/8/2 下午4:17
 * 描述:生成 解析 验证
 */
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretStr;

    @Value("${jwt.expire}")
    private Long expireTime;

    private SecretKey secretKey;

    // 项目启动初始化加密密钥
    @PostConstruct
    public void init() {
        if (secretStr == null || secretStr.isBlank()) {
            throw new IllegalStateException("JWT 密钥未配置，请设置 JWT_SECRET 环境变量或在 application-local.yml 中配置 jwt.secret");
        }
        byte[] bytes;
        try {
            // 兼容 Base64 形式的密钥
            bytes = Base64.getDecoder().decode(secretStr);
        } catch (IllegalArgumentException e) {
            bytes = null;
        }
        // 非 Base64 或解码后长度不足 32 字节时，用 SHA-256 派生 32 字节密钥，兼容普通字符串
        if (bytes == null || bytes.length < 32) {
            try {
                bytes = MessageDigest.getInstance("SHA-256")
                        .digest(secretStr.getBytes(StandardCharsets.UTF_8));
            } catch (NoSuchAlgorithmException e) {
                throw new IllegalStateException("初始化 JWT 密钥失败", e);
            }
        }
        secretKey = Keys.hmacShaKeyFor(bytes);
    }

    /**
     * 生成token
     * @param userId 用户id
     * @param username 用户名
     * @param role 用户角色（0=管理院，1=普通用户）
     * @return jwt令牌
     */
    public String generateToken(Long userId, String username, Integer role) {
        long now = System.currentTimeMillis();
        Date expireDate = new Date(now + expireTime);

        return Jwts.builder()
                .setSubject(userId.toString()) // 主题：用户id
                .claim("username", username)   // 自定义载荷：用户名
                .claim("role", role)           // 自定义载荷：角色（数字）
                .setIssuedAt(new Date(now))    // 签发时间
                .setExpiration(expireDate)      // 过期时间
                .signWith(secretKey, SignatureAlgorithm.HS256) // 加密
                .compact();
    }

    /**
     * 解析token，获取用户id
     */
    public Long getUserId(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return Long.valueOf(claims.getSubject());
    }

    /**
     * 解析token，获取用户角色
     */
    public Integer getRole(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get("role", Integer.class);
    }

    /**
     * 校验token是否合法、未过期
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            // token过期
        } catch (MalformedJwtException e) {
            // token格式错误
        } catch (SignatureException e) {
            // 签名篡改，非法token
        } catch (Exception e) {
            // 其他错误
        }
        return false;
    }
}