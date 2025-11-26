package com.example.bookmanagementsystembo.auth.domain.service;

import com.example.bookmanagementsystembo.auth.KakaoOAuthClient;
import com.example.bookmanagementsystembo.auth.presentation.dto.KakaoTokenResponse;
import com.example.bookmanagementsystembo.auth.presentation.dto.KakaoUserResponse;
import com.example.bookmanagementsystembo.auth.presentation.dto.LoginResult;
import com.example.bookmanagementsystembo.token.service.TokenService;
import com.example.bookmanagementsystembo.token.dto.TokenRes;
import com.example.bookmanagementsystembo.user.enums.Role;
import com.example.bookmanagementsystembo.user.entity.Users;
import com.example.bookmanagementsystembo.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class KakaoLoginService {

    private final KakaoOAuthClient kakaoOAuthClient;

    private final UserRepository userRepository;

    private final TokenService tokenService;

    private final PasswordEncoder passwordEncoder;

    @Transactional
    public LoginResult login(String authorizationCode) {
        KakaoTokenResponse kakaoTokenResponse = kakaoOAuthClient.getToken(authorizationCode);
        KakaoUserResponse kakaoUserResponse = kakaoOAuthClient.getUserInfo(kakaoTokenResponse.getAccessToken());
        Long kakaoId = kakaoUserResponse.getId();
        String nickname = kakaoUserResponse.getKakaoAccount().getProfile().getNickname();

        String email = "kakao:" + kakaoId;

        Users newUser = Users.create(email, passwordEncoder.encode("a1234"), nickname != null ? nickname : "카카오사용자",null, null, Role.ROLE_USER);

        Users user = userRepository.findByEmail(email).orElseGet(() -> userRepository.save(newUser));

        TokenRes tokens = tokenService.issue(user);
        return LoginResult.of(tokens.accessToken(), tokens.refreshToken(), user.getEmail(), user.getName());
    }
}
