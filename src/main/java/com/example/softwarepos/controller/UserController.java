package com.example.softwarepos.controller;

import com.example.softwarepos.dto.UserDto;
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
    public String signup(@RequestBody UserDto userDto) {
        // 1. 이메일(아이디) 중복 체크
        if (userRepository.existsByEmail(userDto.getEmail())) {
            return "이미 존재하는 이메일(아이디)입니다.";
        }
        // 2. 닉네임아이디 중복 체크
        if (userRepository.existsByNicknameId(userDto.getNicknameId())) {
            return "이미 존재하는 닉네임 ID입니다.";
        }

        // 3. Entity 변환 및 저장
        UserEntity user = new UserEntity();
        user.setEmail(userDto.getEmail()); // 아이디로 사용
        user.setPassword(passwordEncoder.encode(userDto.getPassword())); // 비밀번호 암호화
        user.setNicknameId(userDto.getNicknameId());
        user.setNickname(userDto.getNickname());
        user.setIntroduction(userDto.getIntroduction());
        user.setProfileImage(userDto.getProfileImage()); // 필요시 파일 업로드 로직 별도 추가
        user.setRole("USER");

        userRepository.save(user);
        return "회원가입 완료";
    }

    // =====================
    // 🔹 로그인
    // =====================
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody UserDto loginRequest) {
        Map<String, Object> result = new HashMap<>();
        try {
            // UsernamePasswordAuthenticationToken의 첫 번째 인자는 'Principal(아이디)'입니다.
            // 여기서는 email이 아이디 역할을 하므로 email을 넣습니다.
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );
            
            result.put("success", true);
            result.put("email", authentication.getName()); // 로그인된 이메일 반환
            result.put("message", "로그인 성공");
        } catch (AuthenticationException e) {
            result.put("success", false);
            result.put("message", "이메일 또는 비밀번호가 올바르지 않습니다.");
        }
        return result;
    }

    // =====================
    // 🔹 아이디(이메일) 찾기 (사실상 존재 여부 확인)
    // =====================
    // 이메일 자체가 아이디이므로, '닉네임아이디'를 통해 이메일을 찾거나
    // 혹은 이메일 입력 시 가입 여부를 확인하는 로직으로 변경될 수 있습니다.
    // 여기서는 "해당 이메일로 가입된 계정이 있는지 확인하고 인증코드 전송"하는 흐름으로 유지합니다.
    @PostMapping("/check-email") 
    public Map<String, Object> checkEmailAndSendCode(@RequestBody Map<String, String> request) {
        Map<String, Object> result = new HashMap<>();
        String email = request.get("email");

        // 이메일로 유저 찾기
        Optional<UserEntity> userOpt = userRepository.findByEmail(email);
        
        if (userOpt.isEmpty()) {
            result.put("success", false);
            result.put("message", "가입되지 않은 이메일입니다.");
            return result;
        }

        // 인증 코드 전송
        String code = emailService.sendVerificationCode(email);

        result.put("success", true);
        result.put("message", "인증 코드가 이메일로 전송되었습니다.");
        result.put("verificationCode", code); // 개발용 (실제 배포 시 제거)
        return result;
    }

    // =====================
    // 🔹 비밀번호 찾기
    // =====================
    @PostMapping("/find-password")
    public Map<String, Object> findPassword(@RequestBody Map<String, String> request) {
        Map<String, Object> result = new HashMap<>();
        String email = request.get("email");         // 아이디(이메일)
        String nicknameId = request.get("nicknameId"); // 본인 확인용 닉네임아이디

        // 이메일과 닉네임아이디가 일치하는 계정이 있는지 확인
        Optional<UserEntity> userOpt = userRepository.findByEmailAndNicknameId(email, nicknameId);
        
        if (userOpt.isEmpty()) {
            result.put("success", false);
            result.put("message", "입력하신 정보와 일치하는 계정이 없습니다.");
            return result;
        }

        // 인증 코드 전송
        String code = emailService.sendVerificationCode(email);

        result.put("success", true);
        result.put("message", "인증 코드가 이메일로 전송되었습니다.");
        result.put("verificationCode", code); // 개발용
        return result;
    }

    // =====================
    // 🔹 인증 코드 확인 (변동 없음)
    // =====================
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

    // =====================
    // 🔹 비밀번호 재설정
    // =====================
    @PostMapping("/reset-password")
    public Map<String, Object> resetPassword(@RequestBody Map<String, String> request) {
        Map<String, Object> result = new HashMap<>();
        String email = request.get("email"); // 변경할 계정의 이메일(아이디)
        String newPassword = request.get("newPassword");

        Optional<UserEntity> userOpt = userRepository.findByEmail(email);
        
        if (userOpt.isEmpty()) {
            result.put("success", false);
            result.put("message", "해당 이메일의 계정을 찾을 수 없습니다.");
            return result;
        }

        UserEntity user = userOpt.get();
        user.setPassword(passwordEncoder.encode(newPassword)); // 비밀번호 변경
        userRepository.save(user);

        result.put("success", true);
        result.put("message", "비밀번호가 성공적으로 변경되었습니다.");
        return result;
    }
}