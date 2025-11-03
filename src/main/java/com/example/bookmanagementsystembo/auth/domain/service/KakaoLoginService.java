package com.example.bookmanagementsystembo.auth.domain.service;

import com.example.bookmanagementsystembo.auth.KakaoClient;
import com.example.bookmanagementsystembo.auth.presentation.dto.KakaoTokenResponse;
import com.example.bookmanagementsystembo.auth.presentation.dto.KakaoUserResponse;
import com.example.bookmanagementsystembo.auth.presentation.dto.LoginResult;
import com.example.bookmanagementsystembo.token.domain.dto.CreateTokenDto;
import com.example.bookmanagementsystembo.token.domain.service.TokenService;
import com.example.bookmanagementsystembo.user.domain.dto.enums.Role;
import com.example.bookmanagementsystembo.user.domain.entity.Users;
import com.example.bookmanagementsystembo.user.infra.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class KakaoLoginService {

    private final KakaoClient kakaoClient;

    private final UserRepository userRepository;

    private final TokenService tokenService;

    @Transactional
    public LoginResult login(String authorizationCode) {
        KakaoTokenResponse kakaoTokenResponse = kakaoClient.getToken(authorizationCode);

        KakaoUserResponse kakaoUserResponse = kakaoClient.getUserInfo(kakaoTokenResponse.getAccessToken());

        Long kakaoId = kakaoUserResponse.getId();
        String nickname = kakaoUserResponse.getKakaoAccount().getProfile().getNickname();

        String email = "kakao" + kakaoId + "@";

        Users newUser = Users.create(
                null, "kakao" + kakaoId + "@", null, nickname != null ? nickname : "카카오사용자",
                null, null, Role.ROLE_USER, 0
        );

        Users user = userRepository.findByEmail(email).orElseGet(() -> userRepository.save(newUser));

        CreateTokenDto tokens = tokenService.issue(user.getEmail());
        return LoginResult.of(tokens.accessToken(), tokens.refreshToken(), user.getEmail(), user.getName());
    }
}
