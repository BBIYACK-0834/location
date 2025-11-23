package com.example.softwarepos.repository;

import com.example.softwarepos.entity.PlaceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceRepository extends JpaRepository<PlaceEntity, Long> {
    
}
