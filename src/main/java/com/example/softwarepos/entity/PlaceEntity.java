package com.example.softwarepos.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter 
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 사용을 위해 protected 기본 생성자 추가
@Table(name = "Place")
public class PlaceEntity {
    
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String placename; // 소문자 관례 준수

    private String placeExp;
    private String category;
    private Long likes = 0L; // 초기값을 Java에서 설정
    private String comment; // 주석 (필요시 사용)
    private String productImagePath;
    private String longitude;
    private String latitude;
    
    @Column(nullable = false)
    private String uploaderEmail;

    // 1. 객체 생성은 오직 Builder를 통해서만 가능하도록 합니다.
    @Builder 
    public PlaceEntity(String placename, String placeExp, String category, 
                       String productImagePath, String longitude, String latitude, 
                       String uploaderEmail) {
        this.placename = placename;
        this.placeExp = placeExp;
        this.category = category;
        this.productImagePath = productImagePath;
        this.longitude = longitude;
        this.latitude = latitude;
        this.uploaderEmail = uploaderEmail;
        this.likes = 0L; // 좋아요는 생성 시 0으로 고정
    }

    // 2. 객체 수정은 Semantic Method(의미 있는 메서드)를 통해서만 허용합니다.
    public void update(String placename, String placeExp, String category, 
                       String longitude, String latitude, String productImagePath) {
        this.placename = placename;
        this.placeExp = placeExp;
        this.category = category;
        this.longitude = longitude;
        this.latitude = latitude;
        if (productImagePath != null) {
             this.productImagePath = productImagePath;
        }
    }
    
    // 3. 좋아요 증가는 별도의 Semantic Method로 처리
    public void incrementLikes() {
        this.likes++;
    }
}