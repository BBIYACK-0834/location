package com.example.softwarepos.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    // 32바이트 이상의 비밀키 (보안상 매우 중요, 실제론 properties파일에 숨김)
    private final String SECRET_KEY = "mySecretKeyIsVeryVeryVeryLongAndSecureEnoughToUseForJWT";
    private final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    // 토큰 유효시간 (24시간)
    private final long EXPIRATION_TIME = 1000 * 60 * 60 * 24;

    // 1. 토큰 생성 (로그인 성공 시 호출)
    public String createToken(String email) {
        return Jwts.builder()
                .setSubject(email) // 토큰에 담을 정보 (이메일)
                .setIssuedAt(new Date()) // 발행 시간
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // 만료 시간
                .signWith(key, SignatureAlgorithm.HS256) // 암호화 알고리즘
                .compact();
    }

    // 2. 토큰에서 이메일 추출
    public String getEmailFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // 3. 토큰 유효성 검사
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            System.out.println("유효하지 않은 토큰입니다: " + e.getMessage());
            return false;
        }
    }
}