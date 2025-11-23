package com.example.softwarepos.controller;

import com.example.softwarepos.entity.PlaceEntity;
import com.example.softwarepos.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

@RestController
@RequestMapping("/place")
@RequiredArgsConstructor
public class PlaceController {
    private final PlaceRepository placeRepository;

    // 장소 목록 조회
    @GetMapping("/list")
    public List<PlaceEntity> getPlaces() {
        return placeRepository.findAll();
    }

    // 장소 추가 (이미지 업로드 포함)
    @PostMapping("/add")
    public Map<String, Object> addPlace(
            @ModelAttribute PlaceEntity placeRequest, 
            @RequestParam(value = "uploadFile", required = false) MultipartFile uploadFile) {
        
        Map<String, Object> result = new HashMap<>();
        String uploadDir = "/workspaces/AIShop/uploads"; // 업로드 디렉토리

        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs(); // 디렉토리가 없으면 생성
        }

        try {
            // 이미지 파일 처리
            if (uploadFile != null && !uploadFile.isEmpty()) {
                String originalFilename = uploadFile.getOriginalFilename();  // 원본 파일명
                String uuid = UUID.randomUUID().toString();  // 고유한 UUID 생성
                String savedFilename = uuid + "_" + originalFilename;  // 고유 파일명

                // 파일 저장 경로 설정
                Path filePath = Paths.get(uploadDir, savedFilename);
                Files.copy(uploadFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);  // 파일 저장

                // 이미지 경로를 PlaceEntity에 저장
                placeRequest.setProductImagePath(savedFilename);
            }

            // 장소 저장
            PlaceEntity savedPlace = placeRepository.save(placeRequest);

            result.put("success", true);
            result.put("message", "장소가 성공적으로 추가되었습니다.");
            result.put("place", savedPlace);

        } catch (IOException e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "장소 추가 중 이미지 파일 처리 오류가 발생했습니다.");
        }

        return result;
    }

    // 장소 수정
    @PutMapping("/update/{id}")
    public Map<String, Object> updatePlace(@PathVariable Long id, @RequestBody PlaceEntity placeRequest) {
        Map<String, Object> result = new HashMap<>();

        Optional<PlaceEntity> placeOpt = placeRepository.findById(id);
        if (placeOpt.isPresent()) {
            PlaceEntity place = placeOpt.get();
            place.setPlacename(placeRequest.getPlacename());
            place.setPlaceExp(placeRequest.getPlaceExp());
            place.setCategory(placeRequest.getCategory());
            place.setLikes(placeRequest.getLikes());
            place.setComment(placeRequest.getComment());
            place.setProductImagePath(placeRequest.getProductImagePath());

            PlaceEntity updatedPlace = placeRepository.save(place);
            result.put("success", true);
            result.put("message", "장소가 수정되었습니다.");
            result.put("place", updatedPlace);
        } else {
            result.put("success", false);
            result.put("message", "존재하지 않는 장소입니다.");
        }

        return result;
    }

    // 장소 삭제
    @DeleteMapping("/delete/{id}")
    public Map<String, Object> deletePlace(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();

        if (placeRepository.existsById(id)) {
            placeRepository.deleteById(id);
            result.put("success", true);
            result.put("message", "장소가 삭제되었습니다.");
        } else {
            result.put("success", false);
            result.put("message", "존재하지 않는 장소입니다.");
        }

        return result;
    }
}
