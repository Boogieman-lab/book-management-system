package com.example.bookmanagementsystembo.auth.domain.service;

import com.example.bookmanagementsystembo.auth.presentation.dto.LoginRequest;
import com.example.bookmanagementsystembo.auth.presentation.dto.LoginResponse;
import com.example.bookmanagementsystembo.auth.presentation.dto.SignupRequest;
import com.example.bookmanagementsystembo.exception.CoreException;
import com.example.bookmanagementsystembo.exception.ErrorType;
import com.example.bookmanagementsystembo.token.dto.TokenResponse;
import com.example.bookmanagementsystembo.token.service.TokenService;
import com.example.bookmanagementsystembo.user.entity.Users;
import com.example.bookmanagementsystembo.user.enums.Role;
import com.example.bookmanagementsystembo.user.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    /**
     * 회원가입을 처리합니다.
     * 이미 존재하는 이메일이면 예외를 발생시킵니다.
     * 비밀번호는 BCrypt로 인코딩되어 저장됩니다.
     */
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
                Role.ROLE_USER
        );

        userRepository.save(user);
    }

    /**
     * 이메일/비밀번호로 로그인합니다.
     * <ul>
     *   <li>계정이 잠긴 경우 예외를 발생시킵니다.</li>
     *   <li>비밀번호가 틀리면 실패 횟수를 증가시키며, 5회 실패 시 계정이 잠깁니다.</li>
     *   <li>로그인 성공 시 실패 횟수를 초기화하고 AccessToken/RefreshToken을 발급합니다.</li>
     * </ul>
     */
    @Transactional(noRollbackFor = CoreException.class)
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

        TokenResponse tokenRes = tokenService.issue(user);
        return new LoginResponse(tokenRes.accessToken(), tokenRes.refreshToken(), user.getEmail(), user.getName());
    }
}