package com.example.softwarepos.service;

import com.example.softwarepos.entity.UserEntity;
import com.example.softwarepos.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // [수정 1] 아이디가 '이메일'로 변경되었으므로 findByEmail 사용
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + email));

        // [수정 2] UserEntity의 변경된 필드명(getter) 사용
        return User.builder()
                .username(user.getEmail())       // getUserid() -> getEmail()
                .password(user.getPassword())    // getUserpw() -> getPassword()
                .roles(user.getRole())           // 권한 설정
                .build();
    }
}