package com.example.bookmanagementsystembo.auth.domain.service;

import com.example.bookmanagementsystembo.auth.presentation.dto.LoginRequest;
import com.example.bookmanagementsystembo.auth.presentation.dto.LoginResponse;
import com.example.bookmanagementsystembo.auth.presentation.dto.SignupRequest;
import com.example.bookmanagementsystembo.exception.CoreException;
import com.example.bookmanagementsystembo.exception.ErrorType;
import com.example.bookmanagementsystembo.token.dto.TokenRes;
import com.example.bookmanagementsystembo.token.service.TokenService;
import com.example.bookmanagementsystembo.user.entity.Users;
import com.example.bookmanagementsystembo.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    @Transactional
    public void signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new CoreException(ErrorType.USER_ALREADY_EXISTS, request.email());
        }

        Users user = Users.create(
                request.email(),
                passwordEncoder.encode(request.password()),
                request.username(),
                null,
                null,
                request.role()
        );

        userRepository.save(user);
    }

    @Transactional
    public LoginResponse login(LoginRequest request) {
        Users user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new CoreException(ErrorType.INVALID_CREDENTIALS, request.email()));

        if (user.isLocked()) {
            throw new CoreException(ErrorType.ACCOUNT_LOCKED, request.email());
        }

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            user.incrementLoginFailCount();
            userRepository.save(user);
            throw new CoreException(ErrorType.INVALID_CREDENTIALS, request.email());
        }

        user.resetLoginFailCount();
        userRepository.save(user);

        TokenRes tokenRes = tokenService.issue(user);
        return new LoginResponse(tokenRes.accessToken(), tokenRes.refreshToken(), user.getEmail(), user.getName());
    }
}