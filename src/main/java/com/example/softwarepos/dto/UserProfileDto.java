package com.example.softwarepos.dto;

import lombok.Data;
import java.util.List;

@Data
public class UserProfileDto {
    private String email;
    private String nickname;
    private String nicknameId;
    private String introduction;
    private String profileImage;

    private long postCount;
    private long followerCount;
    private long followingCount;
    
    private List<PostSummary> myPosts; 

    @Data
    public static class PostSummary {
        private String productImage;
        private String latitude;
        private String longitude;
    }
}