package com.example.softwarepos.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

// 업데이트 시 필요한 필드만 포함. 역시 불변 객체로 사용.
@Getter
@NoArgsConstructor 
@AllArgsConstructor
public class PlaceUpdateRequest {
    private String placename;
    private String placeExp;
    private String category;
    private String longitude;
    private String latitude;
    private String productImagePath;
}