package com.example.softwarepos.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    // 이메일 → 인증정보 저장
    private final Map<String, VerificationInfo> verificationMap = new ConcurrentHashMap<>();

    // 인증코드 유효시간 (분)
    private static final int EXPIRATION_MINUTES = 5;

    public String sendVerificationCode(String toEmail) {
        String code = generateVerificationCode();

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("[SoftwarePOS] 이메일 인증 코드");
        message.setText("인증 코드: " + code + "\n5분 이내에 입력해주세요.");

        mailSender.send(message);

        // 코드 저장
        verificationMap.put(toEmail, new VerificationInfo(code, LocalDateTime.now()));

        return code;
    }

    public boolean verifyCode(String email, String code) {
        VerificationInfo info = verificationMap.get(email);
        if (info == null) return false;

        boolean expired = info.createdAt().plusMinutes(EXPIRATION_MINUTES).isBefore(LocalDateTime.now());
        if (expired) {
            verificationMap.remove(email);
            return false;
        }

        if (!info.code().equals(code)) return false;

        // 인증 완료 시 코드 삭제
        verificationMap.remove(email);
        return true;
    }

    private String generateVerificationCode() {
        return String.valueOf(new Random().nextInt(900000) + 100000); // 6자리 난수 생성
    }

    private record VerificationInfo(String code, LocalDateTime createdAt) {}
}
