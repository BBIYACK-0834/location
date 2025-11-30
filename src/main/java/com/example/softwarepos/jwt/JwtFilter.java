package com.example.softwarepos.jwt;

import com.example.softwarepos.service.UserDetailService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailService userDetailService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        String authHeader = request.getHeader("Authorization");

        if (requestURI.startsWith("/place/add")) {
            System.out.println("==================================================");
            System.out.println("🚨 [JwtFilter] 검문 시작: " + requestURI);
        }

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            
            // 1. 토큰 유효성 검사
            if (jwtUtil.validateToken(token)) {
                String email = jwtUtil.getEmailFromToken(token);
                if (requestURI.startsWith("/place/add")) {
                    System.out.println("✅ 토큰 유효함! 토큰 속 이메일: " + email);
                }

                // 2. DB에서 유저 찾기 (여기가 문제일 가능성 높음!)
                try {
                    UserDetails userDetails = userDetailService.loadUserByUsername(email);
                    
                    if (requestURI.startsWith("/place/add")) {
                        System.out.println("✅ DB에서 유저 조회 성공! 권한: " + userDetails.getAuthorities());
                    }

                    // 3. 강제 로그인 처리
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    
                    if (requestURI.startsWith("/place/add")) {
                        System.out.println("🔓 인증 객체(SecurityContext) 저장 완료! (통과 예정)");
                    }

                } catch (Exception e) {
                    // ★★★ 여기가 실행되면 DB에 유저가 없거나, 조회 중 에러가 난 것임 ★★★
                    System.out.println("❌❌❌ 유저 로딩 실패 (범인은 여기다!) ❌❌❌");
                    System.out.println("에러 메시지: " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                if (requestURI.startsWith("/place/add")) System.out.println("❌ 토큰 유효성 검사 탈락");
            }
        }

        filterChain.doFilter(request, response);
    }
}