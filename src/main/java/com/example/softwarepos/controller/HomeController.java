package com.example.softwarepos.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    // 루트 URL ("/") 요청 시 호출됨
    @GetMapping("/")
    public String home() {
        return """
        ✅ 서버 정상 작동 중!

        사용 가능한 엔드포인트 목록:
        - POST /user/signup : 회원가입
        - POST /user/login  : 로그인 (Spring Security 처리)
        """;
    }
}
