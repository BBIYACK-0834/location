package com.example.softwarepos.dto;

import com.example.softwarepos.entity.PlaceEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;

// Getter만 존재하며, Entity를 받아 객체를 생성 (출력 전용)
@Getter
@NoArgsConstructor
public class PlaceResponse {
    private Long id;
    private String placename;
    private String placeExp;
    private String category;
    private Long likes;
    private String productImagePath;
    private String longitude;
    private String latitude;
    private String uploaderEmail; // 작성자 정보 포함
    
    // Entity를 받아서 DTO를 생성하는 생성자
    public PlaceResponse(PlaceEntity entity) {
        this.id = entity.getId();
        this.placename = entity.getPlacename();
        this.placeExp = entity.getPlaceExp();
        this.category = entity.getCategory();
        this.likes = entity.getLikes();
        this.productImagePath = entity.getProductImagePath();
        this.longitude = entity.getLongitude();
        this.latitude = entity.getLatitude();
        this.uploaderEmail = entity.getUploaderEmail();
    }
}