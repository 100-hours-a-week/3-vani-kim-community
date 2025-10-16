package com.vani.week4.backend.auth.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

/**
 * @author vani
 * @since 10/13/25
 */
@Component
public class JwtTokenProvider {
    private final Key key;
    private final long accessExpirationsMs;
    private final long refreshExpirationMs;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.access-expiration-ms}") long accessExpirationMs,
            @Value("${jwt.refresh-expiration-ms}") long refreshExpirationMs
    ) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes); //키 객체 생성
        this.accessExpirationsMs = accessExpirationMs;
        this.refreshExpirationMs = refreshExpirationMs;
    }

    // Access Token
    public String generateAccessToken(String userId) {
        return generateToken(userId, accessExpirationsMs);
    }

    //Refresh Token
    public String generateRefreshToken(String userId) {
        return generateToken(userId, refreshExpirationMs);
    }

    /**
     * 토큰을 생성하는 로직 , userId/발급시간/만료/시간 설정, 서명 후 토큰 생성
     *
     * @param userId: 토큰 주체
     * @param expirationMs: 만료 시간
     * @return 토큰 반환
     * */
    private String generateToken(String userId, long expirationMs) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 리프레시 토큰 검증 메서드
     * */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getUserIdFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}