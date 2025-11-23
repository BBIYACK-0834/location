package com.example.softwarepos.controller;

import com.example.softwarepos.entity.ProductEntity;
import com.example.softwarepos.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductRepository productRepository;
    
    @GetMapping("/list") // 상품 확인
    public List<ProductEntity> getProducts() {
    return productRepository.findAll();
}


    @PostMapping("/add")
public Map<String, Object> addProduct(
        @ModelAttribute ProductEntity productRequest,
        @RequestParam(value = "uploadFile", required = false) MultipartFile uploadFile) {

    Map<String, Object> result = new HashMap<>();
    String uploadDir = "/workspaces/AIShop/uploads";
    File dir = new File(uploadDir);
    if (!dir.exists()) {
        dir.mkdirs();
    }

    try {
        // 이미지 파일 처리
        if (uploadFile != null && !uploadFile.isEmpty()) {
            String originalFilename = uploadFile.getOriginalFilename();
            String uuid = UUID.randomUUID().toString();
            String savedFilename = uuid + "_" + originalFilename;

            Path filePath = Paths.get(uploadDir, savedFilename);
            Files.copy(uploadFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // 이미지 경로를 상품 엔티티에 저장 (필드 추가 필요)
            productRequest.setProductImagePath(savedFilename);
        }

        // 상품 저장
        ProductEntity savedProduct = productRepository.save(productRequest);

        result.put("success", true);
        result.put("message", "상품이 성공적으로 추가되었습니다.");
        result.put("product", savedProduct);

    } catch (Exception e) {
        e.printStackTrace();
        result.put("success", false);
        result.put("message", "상품 추가 중 오류가 발생했습니다.");
    }

    return result;
}

    @PutMapping("/update/{id}")
    public Map<String, Object> updateProduct(@PathVariable Long id,
        @RequestBody ProductEntity productRequest) {
        Map<String, Object> result = new HashMap<>();

        productRepository.findById(id).ifPresentOrElse(product -> {
            product.setProname(productRequest.getProname());
            product.setProprice(productRequest.getProprice());
            product.setProsub(productRequest.getProsub());
            product.setProintro(productRequest.getProintro());

            ProductEntity updated = productRepository.save(product);
            result.put("message", "상품이 수정되었습니다.");
            result.put("product", updated);
        }, () -> {
            
            result.put("message", "존재하지 않는 상품입니다.");
        });

        return result;
    }

    @DeleteMapping("/delete/{id}")
    public Map<String, Object> deleteProduct(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();

        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
        
            result.put("message", "상품이 삭제되었습니다.");
        } else {
            
            result.put("message", "존재하지 않는 상품입니다.");
        }

        return result;
    }
    
    }