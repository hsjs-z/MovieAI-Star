package com.example.movie.Utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

/**
 * 作者: 枷
 * 时间:2026/8/2 下午4:17
 * 描述:生成 解析 验证
 */
//@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretStr;

    @Value("${jwt.expire}")
    private Long expireTime;

    private SecretKey secretKey;

    // 项目启动初始化加密密钥
    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretStr);
        secretKey = Keys.hmacShaKeyFor(bytes);
    }

    /**
     * 生成token
     * @param userId 用户id
     * @param username 用户名
     * @return jwt令牌
     */
    public String generateToken(Long userId, String username) {
        long now = System.currentTimeMillis();
        Date expireDate = new Date(now + expireTime);

        return Jwts.builder()
                .setSubject(userId.toString()) // 主题：用户id
                .claim("username", username)   // 自定义载荷
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
