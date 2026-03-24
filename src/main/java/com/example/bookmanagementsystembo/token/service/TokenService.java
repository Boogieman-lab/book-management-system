package com.example.bookmanagementsystembo.token.service;

import com.example.bookmanagementsystembo.exception.CoreException;
import com.example.bookmanagementsystembo.exception.ErrorType;
import com.example.bookmanagementsystembo.token.config.JwtPayloadInfo;
import com.example.bookmanagementsystembo.token.config.JwtTokenProvider;
import com.example.bookmanagementsystembo.token.dto.Token;
import com.example.bookmanagementsystembo.token.dto.TokenResponse;
import com.example.bookmanagementsystembo.token.repository.TokenRepository;
import com.example.bookmanagementsystembo.user.entity.Users;
import com.example.bookmanagementsystembo.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtTokenProvider jwtTokenProvider;

    private final TokenRepository tokenRepository;

    private final StringRedisTemplate redisTemplate;

    private static final String BL_ACCESS_PREFIX = "bl:access:";

    private final UserRepository userRepository;

    /**
     * 사용자에게 AccessToken과 RefreshToken을 발급합니다.
     * RefreshToken은 Redis에 저장되며, 만료 시간은 {@code jwt.refresh-sec} 설정을 따릅니다.
     */
    @Transactional
    public TokenResponse issue(Users user) {
        String userEmail = user.getEmail();

        String accessToken = jwtTokenProvider.createAccessToken(userEmail, user.getRole(), user.getUserId());
        String refreshToken = jwtTokenProvider.createRefreshToken(userEmail);
        long expirationSec = jwtTokenProvider.getRefreshSec();

        Token token = Token.create(userEmail, refreshToken, expirationSec);
        tokenRepository.save(token);

        return TokenResponse.of(accessToken, refreshToken);
    }

    /**
     * RefreshToken을 검증하고 새 AccessToken/RefreshToken을 재발급합니다 (RTR 방식).
     * 토큰이 유효하지 않거나, Redis에 저장된 토큰과 불일치하면 예외를 발생시킵니다.
     * 불일치 시 탈취로 간주하여 저장된 토큰을 삭제합니다.
     */
    @Transactional
    public TokenResponse reissue(String clientRefreshToken) {
        if (!jwtTokenProvider.isValid(clientRefreshToken)) {
            throw new CoreException(ErrorType.TOKEN_INVALID, clientRefreshToken);
        }

        String email = jwtTokenProvider.getUsername(clientRefreshToken);
        Token token = tokenRepository.findById(email).orElseThrow(() -> new CoreException(ErrorType.TOKEN_NOT_FOUND, email));

        if (!token.getToken().equals(clientRefreshToken)) {
            tokenRepository.delete(token);
            throw new CoreException(ErrorType.TOKEN_MISMATCH, clientRefreshToken);
        }

        Users user = userRepository.findByEmail(email).orElseThrow(() -> new CoreException(ErrorType.USER_NOT_FOUND, email));
        String newAccessToken = jwtTokenProvider.createAccessToken(email, user.getRole(), user.getUserId());
        String newRefreshToken = jwtTokenProvider.createRefreshToken(email);
        long expirationSeconds = jwtTokenProvider.getRefreshSec();

        token.updateToken(newRefreshToken, expirationSeconds);
        tokenRepository.save(token);
        return TokenResponse.of(newAccessToken, newRefreshToken);
    }

    /**
     * 로그아웃 처리합니다.
     * Redis에서 RefreshToken을 삭제하고, AccessToken의 남은 유효 시간 동안 블랙리스트에 등록합니다.
     * 토큰이 이미 만료됐거나 JTI가 없으면 블랙리스트 등록을 생략합니다.
     */
    public void logout(String userEmail, String accessToken) {
        tokenRepository.deleteById(userEmail);

        JwtPayloadInfo payloadInfo = jwtTokenProvider.getPayloadInfo(accessToken);

        long remainingTime = payloadInfo.remainingTime();
        String jti = payloadInfo.jti();
        if(remainingTime <= 0 || jti == null || jti.isBlank()) {
            return;
        }

        redisTemplate.opsForValue().set(
                BL_ACCESS_PREFIX + jti,
                "logout",
                remainingTime,
                TimeUnit.SECONDS
        );
    }
}
