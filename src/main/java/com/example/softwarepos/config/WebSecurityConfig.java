package com.example.softwarepos.config;

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
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig implements WebMvcConfigurer {

    private final UserDetailService userService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .cors(cors -> {}) // CORS 활성화
                .csrf(AbstractHttpConfigurer::disable) // CSRF 비활성화 (API 서버용)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // JWT 등 무상태 세션 정책 권장
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/**").permitAll() // 회원가입/로그인/H2 허용
                        .anyRequest().authenticated() // 나머지는 인증 필요
                )
                // ✅ H2 콘솔용 프레임 허용
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))
                .formLogin(AbstractHttpConfigurer::disable) // 기본 로그인 폼 비활성화
                .httpBasic(AbstractHttpConfigurer::disable) // 기본 인증 비활성화
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

    // ✅ Codespaces 환경 CORS 허용
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(
                        "https://*.github.dev",
                        "https://scaling-rotary-phone-gppj7946w4jc9wj-8080.app.github.dev"
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
