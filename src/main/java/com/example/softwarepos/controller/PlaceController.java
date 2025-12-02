package com.example.softwarepos.controller;

import com.example.softwarepos.dto.PlaceCreateRequest;
import com.example.softwarepos.dto.PlaceResponse;
import com.example.softwarepos.dto.PlaceUpdateRequest;
import com.example.softwarepos.service.PlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/place")
@RequiredArgsConstructor
public class PlaceController {

    // Controller는 Repository를 직접 주입받지 않고, Service만 주입받습니다.
    private final PlaceService placeService; 

    // ==========================
    // 1. 장소 목록 조회
    // ==========================
    @GetMapping("/list")
    public List<PlaceResponse> getPlaces() {
        // Service에 위임하고 Response DTO를 반환
        return placeService.getPlaceList(); 
    }

    
    // ==========================
    // 2. 장소 추가 (POST)
    // ==========================
    @PostMapping("/add")
    public ResponseEntity<PlaceResponse> addPlace(
            @ModelAttribute PlaceCreateRequest requestDto, // 수정된 DTO 사용
            @RequestParam(value = "uploadFile", required = false) MultipartFile uploadFile) {

        try {
            // 모든 비즈니스 로직(파일 저장, Entity 생성)은 Service에 위임
            PlaceResponse newPlace = placeService.createPlace(requestDto, uploadFile);
            
            // HTTP 201 Created 상태 코드 반환
            return new ResponseEntity<>(newPlace, HttpStatus.CREATED); 

        } catch (IOException e) {
            e.printStackTrace();
            // 파일 저장 오류 등 발생 시 500 Internal Server Error 반환
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (Exception e) {
            // 그 외 일반적인 오류 처리
            return ResponseEntity.badRequest().build();
        }
    }

    
    // ==========================
    // 3. 장소 수정 (PUT)
    // ==========================
    @PutMapping("/update/{id}")
    public ResponseEntity<PlaceResponse> updatePlace(
            @PathVariable Long id, 
            @RequestBody PlaceUpdateRequest requestDto) { // 수정된 DTO 사용
        try {
            // Service에 위임
            PlaceResponse updatedPlace = placeService.updatePlace(id, requestDto);
            return ResponseEntity.ok(updatedPlace);

        } catch (IllegalArgumentException e) {
            // 존재하지 않는 장소일 경우 404 Not Found 또는 400 Bad Request
            return ResponseEntity.notFound().build();
        } catch (SecurityException e) {
            // 권한이 없을 경우 403 Forbidden
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    // ==========================
    // 4. 장소 삭제 (DELETE)
    // ==========================
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deletePlace(@PathVariable Long id) {
        try {
            placeService.deletePlace(id);
            // 성공적으로 처리했으나 응답 본문이 없을 경우 204 No Content 반환
            return ResponseEntity.noContent().build(); 
        } catch (IllegalArgumentException e) {
            // 존재하지 않는 장소일 경우
            return ResponseEntity.notFound().build();
        }
        // (권한 확인 로직 추가 필요)
    }
}