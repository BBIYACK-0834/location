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
@CrossOrigin(originPatterns = "*", allowCredentials = "true") // CORS 허용 (프론트엔드 연동 필수)
public class PlaceController {

    private final PlaceRepository placeRepository;

    // 이미지 저장 경로 (WebMvcConfig와 일치해야 함)
    private final String UPLOAD_DIR = "/workspaces/AIShop/uploads/";

    // ==========================
    // 1. 장소 목록 조회
    // ==========================
    @GetMapping("/list")
    public List<PlaceEntity> getPlaces() {
        return placeRepository.findAll();
    }

    @PostMapping("/add")
    public Map<String, Object> addPlace(
            @ModelAttribute AddPlaceDto placeDto,
            @RequestParam(value = "uploadFile", required = false) MultipartFile uploadFile) {

        Map<String, Object> result = new HashMap<>();
                System.out.println("=================================");
    System.out.println("🚀 [장소 등록 요청 도착]");
    System.out.println("1. 장소명: " + placeDto.getPlacename());
    System.out.println("2. 위도: " + placeDto.getLatitude());
    System.out.println("3. 경도: " + placeDto.getLongitude());
    System.out.println("4. 작성자(uploaderEmail): " + placeDto.getUploaderEmail()); // ★ 여기가 null이면 프론트 문제!
    System.out.println("=================================");
        File dir = new File(UPLOAD_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        try {
           
            PlaceEntity place = new PlaceEntity();
            place.setPlacename(placeDto.getPlacename());
            place.setPlaceExp(placeDto.getPlaceExp());
            place.setCategory(placeDto.getCategory());
            place.setLongitude(placeDto.getLongitude());
            place.setLatitude(placeDto.getLatitude());
            
            // ★ [핵심 수정] 작성자 이메일 저장 (이게 있어야 마이페이지에 뜸!)
            place.setUploaderEmail(placeDto.getUploaderEmail());

            // 기본값 설정
            place.setLikes(0L);
            place.setComment("");

            // 이미지 파일 처리
            if (uploadFile != null && !uploadFile.isEmpty()) {
                String originalFilename = uploadFile.getOriginalFilename();
                String uuid = UUID.randomUUID().toString();
                String savedFilename = uuid + "_" + originalFilename;

                Path filePath = Paths.get(UPLOAD_DIR + savedFilename);
                Files.copy(uploadFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                place.setProductImagePath(savedFilename);
            } else {
                place.setProductImagePath(""); // 이미지가 없으면 빈 문자열
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

            // 수정 가능한 필드만 업데이트
            place.setPlacename(placeDto.getPlacename());
            place.setPlaceExp(placeDto.getPlaceExp());
            place.setCategory(placeDto.getCategory());
            place.setLongitude(placeDto.getLongitude());
            place.setLatitude(placeDto.getLatitude());
            
            // 이미지가 변경되었다면 업데이트 (null이 아닐 때만)
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