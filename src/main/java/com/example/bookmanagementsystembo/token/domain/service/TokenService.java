package com.example.bookmanagementsystembo.token.domain.service;

import com.example.bookmanagementsystembo.token.config.JwtPayloadInfo;
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

    private static final String BL_ACCESS_PREFIX = "bl:access:";

    private final UserRepository userRepository;

    @Transactional
    public CreateTokenDto issue(String userEmail) {
        Users user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new CoreException(ErrorType.USER_NOT_FOUND, userEmail));

        String accessToken = jwtTokenProvider.createAccessToken(user.getEmail(), user.getRole());
        String refreshToken = jwtTokenProvider.createRefreshToken(userEmail);
        long expirationSec = jwtTokenProvider.getRefreshSec();

        Token token = Token.create(userEmail, refreshToken, expirationSec);
        tokenRepository.save(token);

        return CreateTokenDto.of(accessToken, refreshToken);
    }

    @Transactional
    public CreateTokenDto reissue(String clientRefreshToken) {
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
        String newAccessToken = jwtTokenProvider.createAccessToken(email, user.getRole());
        String newRefreshToken = jwtTokenProvider.createRefreshToken(email);
        long expirationSeconds = jwtTokenProvider.getRefreshSec();

        token.updateToken(newRefreshToken, expirationSeconds);
        tokenRepository.save(token);
        return CreateTokenDto.of(newAccessToken, newRefreshToken);
    }

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
