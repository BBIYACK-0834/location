package com.example.softwarepos.repository;

import com.example.softwarepos.entity.FollowEntity;
import com.example.softwarepos.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

import java.util.Optional;

public interface FollowRepository extends JpaRepository<FollowEntity, Long> {
    // 내가 상대방을 팔로우하고 있는지 확인
    Optional<FollowEntity> findByFollowerAndFollowing(UserEntity follower, UserEntity following);
    
    // 팔로우 취소 시 삭제용
    void deleteByFollowerAndFollowing(UserEntity follower, UserEntity following);

    // [추가됨] 내가 팔로우하는 사람 목록 찾기
    List<FollowEntity> findByFollower(UserEntity follower);

    // [추가됨] 나를 팔로우하는 사람(팔로워) 목록 찾기
    List<FollowEntity> findByFollowing(UserEntity following);

    // 팔로워/팔로잉 수 셀 때 사용 (선택 사항)
    long countByFollower(UserEntity follower);
    long countByFollowing(UserEntity following);
}