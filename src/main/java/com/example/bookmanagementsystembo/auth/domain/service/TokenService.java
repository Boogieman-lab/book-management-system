package com.example.bookmanagementsystembo.auth.domain.service;

import com.example.bookmanagementsystembo.auth.Infra.TokenRepository;
import com.example.bookmanagementsystembo.auth.config.JwtTokenProvider;
import com.example.bookmanagementsystembo.auth.domain.dto.TokenDto;
import com.example.bookmanagementsystembo.auth.domain.entity.Jwt;
import com.example.bookmanagementsystembo.exception.CoreException;
import com.example.bookmanagementsystembo.exception.ErrorType;
import com.example.bookmanagementsystembo.user.domain.entity.Users;
import com.example.bookmanagementsystembo.user.infra.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@RequiredArgsConstructor
@Service
public class TokenService {

    private final TokenRepository tokenRepository;

    private final UserRepository userRepository;

    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public TokenDto createToken(String email, String role) {
        String accessToken = jwtTokenProvider.createAccessToken(email, role);
        String refreshToken = jwtTokenProvider.createRefreshToken(email);

        tokenRepository.deleteByEmail(email);

        Instant issuedAt = Instant.now();
        Instant expiredAt = issuedAt.plusSeconds(jwtTokenProvider.getRefreshSec());

        Jwt jwt = Jwt.create(email, refreshToken, expiredAt, issuedAt);
        tokenRepository.save(jwt);

        return TokenDto.of(accessToken, refreshToken);
    }

    @Transactional
    public TokenDto createNewAccessToken(String refreshToken) {
        if(!jwtTokenProvider.validate(refreshToken)) {
            throw new CoreException(ErrorType.REFRESH_TOKEN_EXPIRED, refreshToken);
        }

        String email = jwtTokenProvider.getUsername(refreshToken);

        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CoreException(ErrorType.USER_NOT_FOUND, email));

        tokenRepository.deleteByEmail(email);

        String newAccessToken = jwtTokenProvider.createAccessToken(user.getEmail(), user.getRole().toString());
        String newRefreshToken = jwtTokenProvider.createRefreshToken(user.getEmail());

        Instant issuedAt = Instant.now();
        Instant expiredAt = issuedAt.plusSeconds(jwtTokenProvider.getRefreshSec());

        Jwt newJwt = Jwt.create(user.getEmail(), newRefreshToken, expiredAt, issuedAt);
        tokenRepository.save(newJwt);

        return TokenDto.of(newAccessToken, newRefreshToken);
    }

    @Transactional
    public void deleteByValue(String refreshToken) {
        Jwt jwt = tokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new CoreException(ErrorType.TOKEN_NOT_FOUND, refreshToken));
        tokenRepository.delete(jwt);
    }
}
