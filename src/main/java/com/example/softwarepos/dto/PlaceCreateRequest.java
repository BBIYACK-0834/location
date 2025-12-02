package com.example.softwarepos.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

// Setter 제거, Builder를 통해 JSON 자동 매핑 및 불변성 확보
@Getter
@NoArgsConstructor // @ModelAttribute/JSON 매핑을 위해 필요
@AllArgsConstructor
public class PlaceCreateRequest {
    // Controller에서 @ModelAttribute로 받기 때문에 Setter 대신 생성자가 필요함

    private String placename;
    private String placeExp;
    private String category;
    private String longitude;
    private String latitude;
    
    // uploaderEmail, Likes, Comment 등은 서버에서 처리하므로 DTO에서 받지 않음.
}