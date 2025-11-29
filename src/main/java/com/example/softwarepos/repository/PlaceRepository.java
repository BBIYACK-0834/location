package com.example.softwarepos.repository;

import com.example.softwarepos.entity.PlaceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; 

public interface PlaceRepository extends JpaRepository<PlaceEntity, Long> {
    
    // [추가] 특정 이메일(작성자)로 작성된 게시물 목록 찾기
    List<PlaceEntity> findByUploaderEmail(String uploaderEmail);

    // [추가] 특정 이메일(작성자)이 작성한 게시물 개수 세기
    long countByUploaderEmail(String uploaderEmail);
}