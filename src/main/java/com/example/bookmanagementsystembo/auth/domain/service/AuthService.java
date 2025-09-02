package com.example.bookmanagementsystembo.auth.domain.service;

import com.example.bookmanagementsystembo.auth.presentation.dto.SignupRequest;
import com.example.bookmanagementsystembo.user.domain.entity.Users;
import com.example.bookmanagementsystembo.user.infra.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 회원가입
     * @param request
     */
    @Transactional
    public void signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("이미 가입되어 있는 유저입니다");
        }

        Users user = Users.of(
                null, // userId는 DB가 자동 생성
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getUsername(), // name
                null, // departmentId 없으면 null
                "default-profile.png", // 기본 프로필 이미지
                request.getRole(),
                0 // loginFailCount 기본 0
        );

        userRepository.save(user);
    }
}
