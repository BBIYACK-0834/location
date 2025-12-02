package com.example.softwarepos.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@NoArgsConstructor
@ToString
public class UserDto {
    
    // 닉네임아이디(@BBIYACK-0834 같은 아이디)
    private String nicknameId;

    // 닉네임(남윤형 같은 이름)
    private String nickname;

    // 아이디(이메일, 로그인용)
    private String email;

    // 비밀번호
    private String password;

    // 소개
    private String introduction;

    private String profileImage; 

    
}