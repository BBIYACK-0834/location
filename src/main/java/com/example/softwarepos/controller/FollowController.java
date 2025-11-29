package com.example.softwarepos.controller;

import com.example.softwarepos.dto.FollowDto;
import com.example.softwarepos.dto.UserDto;
import com.example.softwarepos.entity.FollowEntity;
import com.example.softwarepos.entity.UserEntity;
import com.example.softwarepos.repository.FollowRepository;
import com.example.softwarepos.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/follow")
@RequiredArgsConstructor
public class FollowController {

    private final UserRepository userRepository;
    private final FollowRepository followRepository;

    // ==========================================
    // 1. 팔로우 / 언팔로우 토글 (DTO로 요청 받음)
    // ==========================================
    @PostMapping("/toggle")
    @Transactional
    public Map<String, Object> toggleFollow(@RequestBody FollowDto followDto) {
        Map<String, Object> result = new HashMap<>();

        // DTO에서 이메일 꺼내기
        String myEmail = followDto.getFollowerEmail();      // 나
        String targetEmail = followDto.getFollowingEmail(); // 상대방

        UserEntity me = userRepository.findByEmail(myEmail)
                .orElseThrow(() -> new IllegalArgumentException("내 계정을 찾을 수 없습니다."));
        UserEntity target = userRepository.findByEmail(targetEmail)
                .orElseThrow(() -> new IllegalArgumentException("상대방 계정을 찾을 수 없습니다."));

        // 팔로우 여부 확인
        Optional<FollowEntity> follow = followRepository.findByFollowerAndFollowing(me, target);

        if (follow.isPresent()) {
            // 이미 있으면 -> 언팔로우 (삭제)
            followRepository.delete(follow.get());
            result.put("status", "unfollowed");
            result.put("message", "팔로우 취소");
        } else {
            // 없으면 -> 팔로우 (추가)
            FollowEntity newFollow = new FollowEntity();
            newFollow.setFollower(me);
            newFollow.setFollowing(target);
            followRepository.save(newFollow);
            result.put("status", "followed");
            result.put("message", "팔로우 성공");
        }
        result.put("success", true);
        return result;
    }

    // ==========================================
    // 2. 팔로우 여부 확인 (버튼 색깔용)
    // ==========================================
    @PostMapping("/check")
    public boolean checkFollow(@RequestBody FollowDto followDto) {
        String myEmail = followDto.getFollowerEmail();
        String targetEmail = followDto.getFollowingEmail();

        UserEntity me = userRepository.findByEmail(myEmail).orElse(null);
        UserEntity target = userRepository.findByEmail(targetEmail).orElse(null);

        if (me != null && target != null) {
            return followRepository.findByFollowerAndFollowing(me, target).isPresent();
        }
        return false;
    }

    // ==========================================
    // 3. [핵심] 팔로워 리스트 조회 (나를 팔로우하는 사람들)
    // ==========================================
    @GetMapping("/follower-list/{myEmail}")
    public List<FollowDto> getFollowerList(@PathVariable String myEmail) {
        // 1. 내 정보 찾기
        UserEntity me = userRepository.findByEmail(myEmail).orElseThrow();

        // 2. 나를(following=me) 향한 화살표들을 다 가져옴
        List<FollowEntity> followList = followRepository.findByFollowing(me);

        // 3. Entity -> DTO 변환 (매핑)
        List<FollowDto> dtoList = new ArrayList<>();

        for (FollowEntity follow : followList) {
            FollowDto dto = new FollowDto();
            
            // ★ 핵심: 화살표를 쏜 사람(Follower)의 정보를 꺼내 담음
            UserEntity fan = follow.getFollower(); 

            dto.setFollowerEmail(fan.getEmail());
            dto.setFollowerNickname(fan.getNickname());
            dto.setFollowerProfileImage(fan.getProfileImage());
            
            // 맞팔 확인 등 추가 로직이 필요하면 여기서 처리 가능
            
            dtoList.add(dto);
        }
        return dtoList;
    }

    // ==========================================
    // 4. [핵심] 팔로잉 리스트 조회 (내가 팔로우하는 사람들)
    // ==========================================
    @GetMapping("/following-list/{myEmail}")
    public List<FollowDto> getFollowingList(@PathVariable String myEmail) {
        UserEntity me = userRepository.findByEmail(myEmail).orElseThrow();

        // 1. 내가(follower=me) 쏜 화살표들을 다 가져옴
        List<FollowEntity> followList = followRepository.findByFollower(me);

        List<FollowDto> dtoList = new ArrayList<>();

        for (FollowEntity follow : followList) {
            FollowDto dto = new FollowDto();

            // ★ 핵심: 화살표를 맞은 사람(Following)의 정보를 꺼내 담음
            UserEntity star = follow.getFollowing(); 

            dto.setFollowingEmail(star.getEmail());
            dto.setFollowingNickname(star.getNickname());
            dto.setFollowingProfileImage(star.getProfileImage());

            dtoList.add(dto);
        }
        return dtoList;
    }
    
    // ==========================================
    // 5. 유저 검색 (기존 로직 유지하되 UserDto 반환)
    // ==========================================
    @GetMapping("/search")
    public List<UserDto> searchUser(@RequestParam String keyword, @RequestParam String myEmail) {
        // 닉네임Id 또는 닉네임으로 검색
        List<UserEntity> users = userRepository.findByNicknameIdContainingOrNicknameContaining(keyword, keyword);

        return users.stream()
                .filter(u -> !u.getEmail().equals(myEmail)) // 나 자신 제외
                .map(u -> {
                    UserDto dto = new UserDto();
                    dto.setNicknameId(u.getNicknameId());
                    dto.setNickname(u.getNickname());
                    dto.setEmail(u.getEmail());
                    dto.setProfileImage(u.getProfileImage());
                    dto.setIntroduction(u.getIntroduction());
                    return dto;
                })
                .collect(Collectors.toList());
    }
}