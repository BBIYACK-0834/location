package com.example.softwarepos.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@NoArgsConstructor
@ToString
public class UserDto {
    
    // 닉네임아이디
    private String nicknameId;

    // 닉네임
    private String nickname;

    // 아이디(이메일)
    private String email;

    // 비밀번호
    private String password;

    // 소개
    private String introduction;

    // 프로필 사진 (경로를 받을 수도 있고, 컨트롤러에서 MultipartFile로 별도 처리할 수도 있음)
    // 여기서는 일단 경로(String) 혹은 파일명으로 둡니다.
    private String profileImage; 
}