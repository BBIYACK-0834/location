package com.example.softwarepos.controller;

import com.example.softwarepos.entity.UserEntity;
import com.example.softwarepos.repository.UserRepository;
import com.example.softwarepos.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    // =====================
    // 🔹 회원가입
    // =====================
    @PostMapping("/signup")
    public String signup(@RequestBody UserEntity user) {
        if (userRepository.findByUserid(user.getUserid()).isPresent()) {
            return "이미 존재하는 아이디입니다.";
        }
        user.setUserpw(passwordEncoder.encode(user.getUserpw()));
        userRepository.save(user);
        return "회원가입 완료";
    }

    // =====================
    // 🔹 로그인
    // =====================
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody UserEntity loginRequest) {
        Map<String, Object> result = new HashMap<>();
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUserid(),
                            loginRequest.getUserpw()
                    )
            );
            result.put("success", true);
            result.put("userid", authentication.getName());
        } catch (AuthenticationException e) {
            result.put("success", false);
            result.put("message", "아이디 또는 비밀번호가 올바르지 않습니다.");
        }
        return result;
    }

    
    @PostMapping("/find-id")
    public Map<String, Object> findId(@RequestBody Map<String, String> request) {
        Map<String, Object> result = new HashMap<>();
        String email = request.get("email");

        Optional<UserEntity> userOpt = userRepository.findByUseremail(email);
        if (userOpt.isEmpty()) {
            result.put("success", false);
            result.put("message", "해당 이메일로 가입된 계정이 없습니다.");
            return result;
        }

        String code = emailService.sendVerificationCode(email);

        result.put("success", true);
        result.put("message", "인증 코드가 이메일로 전송되었습니다.");
        result.put("email", email);
        result.put("verificationCode", code); // 개발용
        return result;
    }

    
    @PostMapping("/find-password")
    public Map<String, Object> findPassword(@RequestBody Map<String, String> request) {
        Map<String, Object> result = new HashMap<>();
        String userid = request.get("userid");
        String email = request.get("email");

        Optional<UserEntity> userOpt = userRepository.findByUseridAndUseremail(userid, email);
        if (userOpt.isEmpty()) {
            result.put("success", false);
            result.put("message", "아이디와 이메일이 일치하지 않습니다.");
            return result;
        }

        String code = emailService.sendVerificationCode(email);

        result.put("success", true);
        result.put("message", "인증 코드가 이메일로 전송되었습니다.");
        result.put("verificationCode", code); // 개발용
        return result;
    }

   
    @PostMapping("/verify-code")
    public Map<String, Object> verifyCode(@RequestBody Map<String, String> request) {
        Map<String, Object> result = new HashMap<>();
        String email = request.get("email");
        String code = request.get("code");

        boolean verified = emailService.verifyCode(email, code);
        result.put("success", verified);
        result.put("message", verified ? "인증 성공" : "인증 실패: 코드가 일치하지 않거나 만료됨");
        return result;
    }

    
    @PostMapping("/reset-password")
    public Map<String, Object> resetPassword(@RequestBody Map<String, String> request) {
        Map<String, Object> result = new HashMap<>();
        String userid = request.get("userid");
        String newPassword = request.get("newPassword");

        Optional<UserEntity> userOpt = userRepository.findByUserid(userid);
        if (userOpt.isEmpty()) {
            result.put("success", false);
            result.put("message", "해당 아이디를 찾을 수 없습니다.");
            return result;
        }

        UserEntity user = userOpt.get();
        user.setUserpw(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        result.put("success", true);
        result.put("message", "비밀번호가 성공적으로 변경되었습니다.");
        return result;
    }
}
