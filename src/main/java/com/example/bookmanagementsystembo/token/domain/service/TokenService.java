package com.example.bookmanagementsystembo.token.domain.service;

import com.example.bookmanagementsystembo.token.config.JwtTokenProvider;
import com.example.bookmanagementsystembo.exception.CoreException;
import com.example.bookmanagementsystembo.exception.ErrorType;
import com.example.bookmanagementsystembo.token.domain.dto.CreateTokenDto;
import com.example.bookmanagementsystembo.token.domain.dto.Token;
import com.example.bookmanagementsystembo.token.repository.TokenRepository;
import com.example.bookmanagementsystembo.user.domain.entity.Users;
import com.example.bookmanagementsystembo.user.infra.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtTokenProvider jwtTokenProvider;

    private final TokenRepository tokenRepository;

    private final RedisTemplate<String, String> redisTemplate;

    private static final String BLACKLIST_PREFIX = "AT_BLACKLIST:";

    private final UserRepository userRepository;

    public CreateTokenDto issue(String userId) {
        Users user = userRepository.findByEmail(userId)
                .orElseThrow(() -> new CoreException(ErrorType.USER_NOT_FOUND, userId));

        String accessToken = jwtTokenProvider.createAccessToken(user.getEmail(), user.getRole());
        String refreshToken = jwtTokenProvider.createRefreshToken(userId);
        long expirationSeconds = jwtTokenProvider.getRefreshSec();


        Token token = Token.create(userId, refreshToken, expirationSeconds);
        tokenRepository.save(token);

        return CreateTokenDto.of(accessToken, refreshToken);
    }

    @Transactional
    public CreateTokenDto reissue(String clientRefreshToken) {
        if (!jwtTokenProvider.validate(clientRefreshToken)) {
            throw new CoreException(ErrorType.REFRESH_TOKEN_EXPIRED, clientRefreshToken);
        }

        String email = jwtTokenProvider.getUsername(clientRefreshToken);
        Token token = tokenRepository.findById(email).orElseThrow(() -> new CoreException(ErrorType.TOKEN_NOT_FOUND, email));

        if (!token.getToken().equals(clientRefreshToken)) {
            tokenRepository.delete(token);
            throw new CoreException(ErrorType.TOKEN_MISMATCH, clientRefreshToken);
        }

        Users user = userRepository.findByEmail(email).orElseThrow(() -> new CoreException(ErrorType.USER_NOT_FOUND, email));
        String newAccessToken = jwtTokenProvider.createAccessToken(email, user.getRole());
        String newRefreshToken = jwtTokenProvider.createRefreshToken(email);
        long expirationSeconds = jwtTokenProvider.getRefreshSec();

        token.updateToken(newRefreshToken, expirationSeconds);
        tokenRepository.save(token);
        return CreateTokenDto.of(newAccessToken, newRefreshToken);
    }

    public void logout(String userEmail, String accessToken) {
        tokenRepository.deleteById(userEmail);

        long remainingTime = jwtTokenProvider.getRemainingTime(accessToken);

        if (remainingTime > 0) {
            redisTemplate.opsForValue().set(
                    BLACKLIST_PREFIX + accessToken,
                    "logout refresh token",
                    remainingTime,
                    TimeUnit.SECONDS // TTL 단위를 초로 설정
            );
        }
    }
}
