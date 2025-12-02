package com.example.softwarepos.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@NoArgsConstructor
@Table(name = "users")
public class UserEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 1. 닉네임아이디 (유저 고유 식별자, 중복 불가)
    @Column(unique = true, nullable = false)
    private String nicknameId;

    // 2. 닉네임 (화면 표시용 이름)
    @Column(nullable = false)
    private String nickname;

    // 3. 아이디(이메일) (로그인용, 중복 불가)
    @Column(unique = true, nullable = false)
    private String email;

    // 4. 프로필 사진 (이미지 파일 경로 또는 URL 저장)
    private String profileImage;

    // 5. 소개 (자기소개 텍스트)
    private String introduction;

    // 6. 비밀번호
    @Column(nullable = false)
    private String password;

    // 권한 (기본값 USER)
    private String role = "USER";

    @Column(columnDefinition = "boolean default false")
    private boolean isPrivate = true; // true면 비공개, false면 공개(기본값)
}