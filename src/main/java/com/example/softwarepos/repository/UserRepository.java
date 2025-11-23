package com.example.softwarepos.repository;

import com.example.softwarepos.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUserid(String userid);
    Optional<UserEntity> findByUseraddress(String useraddress);
    Optional<UserEntity> findByUseremail(String useremail);
    Optional<UserEntity> findByUseridAndUseremail(String userid, String useremail);
}
