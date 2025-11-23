package com.example.softwarepos.repository;

import com.example.softwarepos.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    Optional<ProductEntity> findByProname(String proname);
}
