package com.example.softwarepos.config;

import com.example.softwarepos.jwt.JwtFilter; 
import com.example.softwarepos.service.UserDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.http.HttpMethod;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig implements WebMvcConfigurer {

    private final UserDetailService userService;
    private final JwtFilter jwtFilter; // ★ [핵심] JWT 필터 주입

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .cors(cors -> {}) // CORS 설정 활성화 (아래 addCorsMappings 따름)
                .csrf(AbstractHttpConfigurer::disable) // CSRF 끄기 (JWT 사용 시 불필요)
                
                
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                
                .authorizeHttpRequests(auth -> auth
                        // 1. 로그인, 회원가입, 비번찾기 등 인증 없이 접속해야 하는 곳들
                        .requestMatchers(
                                "/user/signup", 
                                "/user/login", 
                                "/user/check-email", 
                                "/user/find-password", 
                                "/user/verify-code", 
                                "/user/reset-password",
                                "/login.html",
                                "/signup.html",
                                "map.html",
                                "/mypage.html",
                                "/js/**",
                                "/images/**", // ★ [핵심] 이미지는 검사하지 마라!
                                "/favicon.ico"
                        ).permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // 2. 정적 리소스(이미지) 및 H2 콘솔 등 허용
                        .requestMatchers("/images/**", "/h2-console/**").permitAll()
                        
                        // 3. 그 외 모든 요청은 인증(토큰)이 있어야만 접근 가능
                        .anyRequest().authenticated()
                )
                
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService);
        authProvider.setPasswordEncoder(bCryptPasswordEncoder());
        return new ProviderManager(List.of(authProvider));
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                // credentials(쿠키,인증헤더) 허용 시에는 * 대신 패턴 사용 필수
                .allowedOriginPatterns("*") 
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}