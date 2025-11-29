package com.example.softwarepos.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@NoArgsConstructor
@ToString
public class FollowDto {

    private Long id; // 팔로우 관계 고유 ID (삭제할 때 유용)

    // ==========================
    // 1. 팔로우 하는 사람 (나, Follower)
    // ==========================
    private String followerEmail;       // 아이디 (이메일)
    private String followerNickname;    // 닉네임
    private String followerProfileImage;// 프사

    // ==========================
    // 2. 팔로우 당하는 사람 (상대방, Following)
    // ==========================
    private String followingEmail;      // 아이디 (이메일)
    private String followingNickname;   // 닉네임
    private String followingProfileImage;// 프사

    // ==========================
    // 3. 상태 체크용 (선택 사항)
    // ==========================
    private boolean matpal; // 맞팔 여부 (내가 쟤를 하는데, 쟤도 나를 하는가?)
}