package com.example.softwarepos.service;

import com.example.softwarepos.entity.UserEntity;
import com.example.softwarepos.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.User;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userid) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByUserid(userid)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return User.withUsername(user.getUserid())
                .password(user.getUserpw())
                .roles(user.getRole())
                .build();
    }
}
