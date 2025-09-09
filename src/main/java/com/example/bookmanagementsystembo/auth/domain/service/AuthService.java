package com.example.bookmanagementsystembo.auth.domain.service;

import com.example.bookmanagementsystembo.auth.presentation.dto.SignupRequest;
import com.example.bookmanagementsystembo.exception.CoreException;
import com.example.bookmanagementsystembo.exception.ErrorType;
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
        if (userRepository.existsByEmail(request.email())) {
            throw new CoreException(ErrorType.USER_ALREADY_EXISTS, request.email());
        }

        Users user = Users.create(
                null, // userId는 DB가 자동 생성
                request.email(),
                passwordEncoder.encode(request.password()),
                request.username(), // name
                null, // departmentId 없으면 null
                "default-profile.png", // 기본 프로필 이미지
                request.role(),
                0 // loginFailCount 기본 0
        );

        userRepository.save(user);
    }
}