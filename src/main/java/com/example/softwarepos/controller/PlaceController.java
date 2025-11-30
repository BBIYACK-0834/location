package com.example.softwarepos.controller;
import com.example.softwarepos.jwt.JwtUtil;
import com.example.softwarepos.dto.AddPlaceDto;
import com.example.softwarepos.entity.PlaceEntity;
import com.example.softwarepos.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.context.SecurityContextHolder;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

@RestController
@RequestMapping("/place")
@RequiredArgsConstructor
public class PlaceController {

    private final PlaceRepository placeRepository;

    // 이미지 저장 경로 (본인의 환경에 맞게 유지)
    private final String UPLOAD_DIR = "/workspaces/location/uploads/";

    // ==========================
    // 1. 장소 목록 조회
    // ==========================
    @GetMapping("/list")
    public List<PlaceEntity> getPlaces() {
        return placeRepository.findAll();
    }

    
    // 2. 장소 추가 (JWT 토큰으로 작성자 식별)
    @PostMapping("/add")
    public Map<String, Object> addPlace(
            @ModelAttribute AddPlaceDto placeDto,
            @RequestParam(value = "uploadFile", required = false) MultipartFile uploadFile) {

        Map<String, Object> result = new HashMap<>();

        File dir = new File(UPLOAD_DIR);
        if (!dir.exists()) dir.mkdirs();

        try {
            PlaceEntity place = new PlaceEntity();
            place.setPlacename(placeDto.getPlacename());
            place.setPlaceExp(placeDto.getPlaceExp());
            place.setCategory(placeDto.getCategory());
            place.setLongitude(placeDto.getLongitude());
            place.setLatitude(placeDto.getLatitude());
            
            // ★ [핵심] 토큰에서 이메일(ID) 꺼내기
            String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            
            System.out.println("토큰 주인 확인: " + currentEmail); // 로그 확인용
            place.setUploaderEmail(currentEmail); // 안전하게 저장!

            place.setLikes(0L);
            place.setComment("");

            if (uploadFile != null && !uploadFile.isEmpty()) {
                String originalFilename = uploadFile.getOriginalFilename();
                String uuid = UUID.randomUUID().toString();
                String savedFilename = uuid + "_" + originalFilename;
                Path filePath = Paths.get(UPLOAD_DIR + savedFilename);
                Files.copy(uploadFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                place.setProductImagePath(savedFilename);
            } else {
                place.setProductImagePath(""); 
            }

            PlaceEntity savedPlace = placeRepository.save(place);

            result.put("success", true);
            result.put("message", "장소가 성공적으로 추가되었습니다.");
            result.put("place", savedPlace);

        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "저장 중 오류 발생: " + e.getMessage());
        }

        return result;
    }

    // ==========================
    // 3. 장소 수정
    // ==========================
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
            
            if (placeDto.getProductImagePath() != null) {
                place.setProductImagePath(placeDto.getProductImagePath());
            }

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

    // ==========================
    // 4. 장소 삭제
    // ==========================
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