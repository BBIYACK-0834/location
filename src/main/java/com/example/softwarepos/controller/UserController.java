package com.example.softwarepos.controller;

import com.example.softwarepos.dto.UserDto;
import com.example.softwarepos.dto.UserProfileDto;
import com.example.softwarepos.entity.UserEntity;
import com.example.softwarepos.repository.FollowRepository; 
import com.example.softwarepos.repository.PlaceRepository;  
import com.example.softwarepos.repository.UserRepository;
import com.example.softwarepos.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.example.softwarepos.jwt.JwtUtil;
import java.util.*;
import java.util.stream.Collectors; 

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final PlaceRepository placeRepository;   // [추가] 게시물 조회용
    private final FollowRepository followRepository; // [추가] 팔로우 수 조회용
    
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final JwtUtil jwtUtil;
    // =====================
    // 🔹 [NEW] 마이페이지 프로필 조회
    // =====================
    @GetMapping("/profile/{email}")
    public UserProfileDto getUserProfile(@PathVariable String email) {
        // 1. 유저 정보 찾기
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        UserProfileDto dto = new UserProfileDto();

        // 2. 기본 정보 매핑
        dto.setEmail(user.getEmail());
        dto.setNickname(user.getNickname());
        dto.setNicknameId(user.getNicknameId());
        dto.setIntroduction(user.getIntroduction());
        dto.setProfileImage(user.getProfileImage());

        // 3. 숫자 통계 (DB 쿼리 실행)
        dto.setPostCount(placeRepository.countByUploaderEmail(email));
        dto.setFollowerCount(followRepository.countByFollowing(user)); // 나를 팔로우한 사람 수
        dto.setFollowingCount(followRepository.countByFollower(user)); // 내가 팔로우한 사람 수

        // 4. 내 게시물 리스트 (지도 표시용 좌표 포함)
        List<UserProfileDto.PostSummary> posts = placeRepository.findByUploaderEmail(email).stream()
                .map(place -> {
                    UserProfileDto.PostSummary summary = new UserProfileDto.PostSummary();
                    summary.setProductImage(place.getProductImagePath());
                    summary.setLatitude(place.getLatitude());
                    summary.setLongitude(place.getLongitude());
                    return summary;
                })
                .collect(Collectors.toList());

        dto.setMyPosts(posts);

        return dto;
    }

    // =====================
    // 🔹 회원가입
    // =====================
    @PostMapping("/signup")
    public String signup(@RequestBody UserDto userDto) {
        if (userRepository.existsByEmail(userDto.getEmail())) {
            return "이미 존재하는 이메일(아이디)입니다.";
        }
        if (userRepository.existsByNicknameId(userDto.getNicknameId())) {
            return "이미 존재하는 닉네임 ID입니다.";
        }

        UserEntity user = new UserEntity();
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setNicknameId(userDto.getNicknameId());
        user.setNickname(userDto.getNickname());
        user.setIntroduction(userDto.getIntroduction());
        user.setProfileImage(userDto.getProfileImage());
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
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );
        
        String token = jwtUtil.createToken(loginRequest.getEmail()); // 토큰 생성
        
        result.put("success", true);
        result.put("email", loginRequest.getEmail());
        result.put("accessToken", token); // ★ 토큰을 담아서 보내야 함!
        result.put("message", "로그인 성공");
        // 👆👆👆👆👆👆

    } catch (AuthenticationException e) {
        result.put("success", false);
        result.put("message", "이메일 또는 비밀번호가 올바르지 않습니다.");
    }
    return result;
}

    // =====================
    // 🔹 이메일 확인 & 인증코드 전송
    // =====================
    @PostMapping("/check-email") 
    public Map<String, Object> checkEmailAndSendCode(@RequestBody Map<String, String> request) {
        Map<String, Object> result = new HashMap<>();
        String email = request.get("email");

        Optional<UserEntity> userOpt = userRepository.findByEmail(email);
        
        if (userOpt.isEmpty()) {
            result.put("success", false);
            result.put("message", "가입되지 않은 이메일입니다.");
            return result;
        }

        String code = emailService.sendVerificationCode(email);

        result.put("success", true);
        result.put("message", "인증 코드가 이메일로 전송되었습니다.");
        result.put("verificationCode", code);
        return result;
    }

    // =====================
    // 🔹 비밀번호 찾기 (이메일 + 닉네임ID)
    // =====================
    @PostMapping("/find-password")
    public Map<String, Object> findPassword(@RequestBody Map<String, String> request) {
        Map<String, Object> result = new HashMap<>();
        String email = request.get("email");
        String nicknameId = request.get("nicknameId");

        Optional<UserEntity> userOpt = userRepository.findByEmailAndNicknameId(email, nicknameId);
        
        if (userOpt.isEmpty()) {
            result.put("success", false);
            result.put("message", "입력하신 정보와 일치하는 계정이 없습니다.");
            return result;
        }

        String code = emailService.sendVerificationCode(email);

        result.put("success", true);
        result.put("message", "인증 코드가 이메일로 전송되었습니다.");
        result.put("verificationCode", code);
        return result;
    }

    // =====================
    // 🔹 인증 코드 검증
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
        String email = request.get("email");
        String newPassword = request.get("newPassword");

        Optional<UserEntity> userOpt = userRepository.findByEmail(email);
        
        if (userOpt.isEmpty()) {
            result.put("success", false);
            result.put("message", "해당 이메일의 계정을 찾을 수 없습니다.");
            return result;
        }

        UserEntity user = userOpt.get();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        result.put("success", true);
        result.put("message", "비밀번호가 성공적으로 변경되었습니다.");
        return result;
    }
    // com.example.softwarepos.controller.UserController 내부

    // =====================
    // 6. [설정] 계정 공개/비공개 전환
    // =====================
    @PutMapping("/visibility")
    public Map<String, Object> updateVisibility(@RequestBody Map<String, Boolean> request) {
        Map<String, Object> result = new HashMap<>();
        
        // 1. 현재 로그인한 사용자 찾기 (JWT 토큰 기반)
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 2. 값 변경
        Boolean isPrivate = request.get("isPrivate");
        user.setPrivate(isPrivate);
        userRepository.save(user);

        result.put("success", true);
        result.put("message", isPrivate ? "계정이 비공개로 전환되었습니다." : "계정이 공개로 전환되었습니다.");
        return result;
    }

    // =====================
    // 7. [설정] 비밀번호 변경 (로그인 상태에서 변경)
    // =====================
    @PutMapping("/change-password")
    public Map<String, Object> changePassword(@RequestBody Map<String, String> request) {
        Map<String, Object> result = new HashMap<>();

        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        String currentPassword = request.get("currentPassword");
        String newPassword = request.get("newPassword");

        // 1. 현재 비밀번호가 맞는지 확인
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            result.put("success", false);
            result.put("message", "현재 비밀번호가 일치하지 않습니다.");
            return result;
        }

        // 2. 새 비밀번호로 업데이트
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        result.put("success", true);
        result.put("message", "비밀번호가 성공적으로 변경되었습니다.");
        return result;
    }
}