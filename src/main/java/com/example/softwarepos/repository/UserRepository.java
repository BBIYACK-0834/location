package com.example.softwarepos.repository;

import com.example.softwarepos.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    
    // 이메일(로그인 ID)로 회원 찾기
    Optional<UserEntity> findByEmail(String email);

    // 닉네임아이디 중복 확인 및 조회용
    Optional<UserEntity> findByNicknameId(String nicknameId);

    // 이메일 존재 여부 (회원가입 중복 체크)
    boolean existsByEmail(String email);

    // 닉네임아이디 존재 여부 (회원가입 중복 체크)
    boolean existsByNicknameId(String nicknameId);

    // 비밀번호 찾기용 (이메일 + 닉네임아이디가 일치하는지 확인)
    Optional<UserEntity> findByEmailAndNicknameId(String email, String nicknameId);
    // 기존 UserRepository 인터페이스 안에 추가
    // 닉네임이나 ID에 검색어가 포함된 유저 찾기 (Like 검색)
    List<UserEntity> findByNicknameIdContainingOrNicknameContaining(String keyword1, String keyword2);
}