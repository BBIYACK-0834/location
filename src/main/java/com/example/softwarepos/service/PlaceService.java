package com.example.softwarepos.service;

import com.example.softwarepos.dto.PlaceCreateRequest;
import com.example.softwarepos.dto.PlaceResponse;
import com.example.softwarepos.dto.PlaceUpdateRequest;
import com.example.softwarepos.entity.PlaceEntity;
import com.example.softwarepos.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.File;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional // 트랜잭션 경계 설정 (쓰기 작업)
public class PlaceService {

    private final PlaceRepository placeRepository;
    // 경로 상수는 Service에 두는 것이 적절 (Controller와 분리)
    private final String UPLOAD_DIR = "/workspaces/location/uploads/"; 
    
    // ===================================
    // 1. 장소 목록 조회 (읽기 전용)
    // ===================================
    @Transactional(readOnly = true)
    public List<PlaceResponse> getPlaceList() {
        return placeRepository.findAll().stream()
                // Entity를 Response DTO로 변환하여 반환
                .map(PlaceResponse::new) 
                .collect(Collectors.toList());
    }

    // ===================================
    // 2. 장소 추가 (Create)
    // ===================================
    public PlaceResponse createPlace(PlaceCreateRequest requestDto, MultipartFile uploadFile) throws IOException {
        
        // 1. [비즈니스 로직] 작성자 이메일 가져오기 (Security Context에서 처리)
        // Controller가 아닌 Service에서 SecurityContext를 사용하는 것이 더 정석입니다.
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        
        // 2. [파일 처리] 파일 저장 로직
        String savedFilename = "";
        if (uploadFile != null && !uploadFile.isEmpty()) {
            File dir = new File(UPLOAD_DIR);
            if (!dir.exists()) dir.mkdirs();

            String originalFilename = uploadFile.getOriginalFilename();
            String uuid = UUID.randomUUID().toString();
            savedFilename = uuid + "_" + originalFilename;
            Path filePath = Paths.get(UPLOAD_DIR + savedFilename);
            Files.copy(uploadFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        }

        // 3. [DTO -> Entity 변환 및 생성] Builder를 사용하여 안전하게 객체 생성
        PlaceEntity place = PlaceEntity.builder()
                .placename(requestDto.getPlacename())
                .placeExp(requestDto.getPlaceExp())
                .category(requestDto.getCategory())
                .longitude(requestDto.getLongitude())
                .latitude(requestDto.getLatitude())
                .productImagePath(savedFilename) // 파일 경로 설정
                .uploaderEmail(currentEmail)    // 작성자 설정
                .build(); // 불변 객체 생성

        // 4. 저장 및 DTO로 변환하여 반환
        PlaceEntity savedPlace = placeRepository.save(place);
        return new PlaceResponse(savedPlace);
    }
    
    // ===================================
    // 3. 장소 수정 (Update)
    // ===================================
    public PlaceResponse updatePlace(Long placeId, PlaceUpdateRequest requestDto) {
        // 1. Entity 조회
        PlaceEntity place = placeRepository.findById(placeId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 장소입니다."));
        
        // 2. [비즈니스 로직] 권한 확인 (Semantic Logic)
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!place.getUploaderEmail().equals(currentEmail)) {
             throw new SecurityException("수정 권한이 없습니다.");
        }

        // 3. [통제된 변경] Entity의 Semantic Method 호출
        place.update(
            requestDto.getPlacename(),
            requestDto.getPlaceExp(),
            requestDto.getCategory(),
            requestDto.getLongitude(),
            requestDto.getLatitude(),
            requestDto.getProductImagePath()
        );

        // JPA의 영속성 컨텍스트 덕분에 save()를 명시적으로 호출하지 않아도 자동으로 변경 감지 후 반영됨.
        // 명시적 호출도 가능: PlaceEntity updatedPlace = placeRepository.save(place); 
        return new PlaceResponse(place);
    }

    // ===================================
    // 4. 장소 삭제 (Delete)
    // ===================================
    public void deletePlace(Long placeId) {
        // 1. Entity 조회 및 권한 확인 (업데이트 로직과 유사)
        PlaceEntity place = placeRepository.findById(placeId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 장소입니다."));
        
        // (권한 확인 로직 추가 가능)

        // 2. 삭제
        placeRepository.delete(place);
    }
}