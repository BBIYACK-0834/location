package com.example.softwarepos.controller;

import com.example.softwarepos.dto.AddPlaceDto;
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

    // 장소 추가 (이미지 업로드 + DTO)
    @PostMapping("/add")
    public Map<String, Object> addPlace(
            @ModelAttribute AddPlaceDto placeDto,
            @RequestParam(value = "uploadFile", required = false) MultipartFile uploadFile) {

        Map<String, Object> result = new HashMap<>();
        String uploadDir = "/workspaces/AIShop/uploads";

        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        try {

            // 새로운 PlaceEntity 생성
            PlaceEntity place = new PlaceEntity();
            place.setPlacename(placeDto.getPlacename());
            place.setPlaceExp(placeDto.getPlaceExp());
            place.setCategory(placeDto.getCategory());
            place.setLongitude(placeDto.getLongitude());
            place.setLatitude(placeDto.getLatitude());

            // 기본값
            place.setLikes(0L);
            place.setComment("");

            // 이미지 처리
            if (uploadFile != null && !uploadFile.isEmpty()) {
                String originalFilename = uploadFile.getOriginalFilename();
                String uuid = UUID.randomUUID().toString();
                String savedFilename = uuid + "_" + originalFilename;

                Path filePath = Paths.get(uploadDir, savedFilename);
                Files.copy(uploadFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                place.setProductImagePath(savedFilename);
            }

            // DB 저장
            PlaceEntity savedPlace = placeRepository.save(place);

            result.put("success", true);
            result.put("message", "장소가 성공적으로 추가되었습니다.");
            result.put("place", savedPlace);

        } catch (IOException e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "이미지 파일 처리 중 오류가 발생했습니다.");
        }

        return result;
    }

    // 장소 수정
    @PutMapping("/update/{id}")
    public Map<String, Object> updatePlace(@PathVariable Long id, @RequestBody PlaceEntity placeDto) {
        Map<String, Object> result = new HashMap<>();

        Optional<PlaceEntity> placeOpt = placeRepository.findById(id);
        if (placeOpt.isPresent()) {
            PlaceEntity place = placeOpt.get();

            place.setPlacename(placeDto.getPlacename());
            place.setPlaceExp(placeDto.getPlaceExp());
            place.setCategory(placeDto.getCategory());
            place.setLongitude(placeDto.getLongitude());
            place.setLatitude(placeDto.getLatitude());
            place.setProductImagePath(placeDto.getProductImagePath());

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
