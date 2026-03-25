package com.example.bookmanagementsystembo.token.service;

import com.example.bookmanagementsystembo.exception.CoreException;
import com.example.bookmanagementsystembo.exception.ErrorType;
import com.example.bookmanagementsystembo.token.config.JwtPayloadInfo;
import com.example.bookmanagementsystembo.token.config.JwtTokenProvider;
import com.example.bookmanagementsystembo.token.dto.Token;
import com.example.bookmanagementsystembo.token.dto.TokenResponse;
import com.example.bookmanagementsystembo.token.repository.TokenRepository;
import com.example.bookmanagementsystembo.user.entity.Users;
import com.example.bookmanagementsystembo.user.enums.Role;
import com.example.bookmanagementsystembo.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    @InjectMocks
    private TokenService tokenService;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private UserRepository userRepository;

    // ──────────────────────────────────────────────────────────────
    // issue
    // ──────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("issue - 토큰 발급")
    class IssueTest {

        @Test
        @DisplayName("성공 - accessToken과 refreshToken이 발급되고 Redis에 저장된다")
        void success() {
            // Given
            Users user = Users.create("user@example.com", "password", "홍길동", 1L, null, Role.ROLE_USER);
            when(jwtTokenProvider.createAccessToken(any(), any(), any())).thenReturn("access-token");
            when(jwtTokenProvider.createRefreshToken(any())).thenReturn("refresh-token");
            when(jwtTokenProvider.getRefreshSec()).thenReturn(300L);
            when(tokenRepository.save(any(Token.class))).thenAnswer(inv -> inv.getArgument(0));

            // When
            TokenResponse result = tokenService.issue(user);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.accessToken()).isEqualTo("access-token");
            assertThat(result.refreshToken()).isEqualTo("refresh-token");
            verify(tokenRepository).save(any(Token.class));
        }
    }

    // ──────────────────────────────────────────────────────────────
    // reissue
    // ──────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("reissue - 토큰 재발급")
    class ReissueTest {

        @Test
        @DisplayName("성공 - 유효한 RefreshToken으로 새 토큰 쌍이 발급된다")
        void success() {
            // Given
            String clientRefreshToken = "valid-refresh-token";
            String email = "user@example.com";
            Token storedToken = Token.create(email, clientRefreshToken, 300L);
            Users user = Users.create(email, "password", "홍길동", 1L, null, Role.ROLE_USER);

            when(jwtTokenProvider.isValid(clientRefreshToken)).thenReturn(true);
            when(jwtTokenProvider.getUsername(clientRefreshToken)).thenReturn(email);
            when(tokenRepository.findById(email)).thenReturn(Optional.of(storedToken));
            when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
            when(jwtTokenProvider.createAccessToken(any(), any(), any())).thenReturn("new-access-token");
            when(jwtTokenProvider.createRefreshToken(any())).thenReturn("new-refresh-token");
            when(jwtTokenProvider.getRefreshSec()).thenReturn(300L);
            when(tokenRepository.save(any(Token.class))).thenAnswer(inv -> inv.getArgument(0));

            // When
            TokenResponse result = tokenService.reissue(clientRefreshToken);

            // Then
            assertThat(result.accessToken()).isEqualTo("new-access-token");
            assertThat(result.refreshToken()).isEqualTo("new-refresh-token");
            verify(tokenRepository).save(any(Token.class));
        }

        @Test
        @DisplayName("실패 - 유효하지 않은 RefreshToken이면 TOKEN_INVALID 예외 발생")
        void fail_invalidToken() {
            // Given
            String invalidToken = "invalid-token";
            when(jwtTokenProvider.isValid(invalidToken)).thenReturn(false);

            // When & Then
            assertThatThrownBy(() -> tokenService.reissue(invalidToken))
                    .isInstanceOf(CoreException.class)
                    .satisfies(ex -> assertThat(((CoreException) ex).getErrorType())
                            .isEqualTo(ErrorType.TOKEN_INVALID));
            verify(tokenRepository, never()).findById(any());
        }

        @Test
        @DisplayName("실패 - Redis에 저장된 토큰이 없으면 TOKEN_NOT_FOUND 예외 발생")
        void fail_tokenNotFound() {
            // Given
            String clientRefreshToken = "valid-refresh-token";
            String email = "user@example.com";
            when(jwtTokenProvider.isValid(clientRefreshToken)).thenReturn(true);
            when(jwtTokenProvider.getUsername(clientRefreshToken)).thenReturn(email);
            when(tokenRepository.findById(email)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> tokenService.reissue(clientRefreshToken))
                    .isInstanceOf(CoreException.class)
                    .satisfies(ex -> assertThat(((CoreException) ex).getErrorType())
                            .isEqualTo(ErrorType.TOKEN_NOT_FOUND));
        }

        @Test
        @DisplayName("실패 - 저장된 토큰과 클라이언트 토큰 불일치 시 TOKEN_MISMATCH 예외 발생 및 토큰 삭제")
        void fail_tokenMismatch() {
            // Given
            String clientRefreshToken = "client-token";
            String email = "user@example.com";
            Token storedToken = Token.create(email, "different-stored-token", 300L);

            when(jwtTokenProvider.isValid(clientRefreshToken)).thenReturn(true);
            when(jwtTokenProvider.getUsername(clientRefreshToken)).thenReturn(email);
            when(tokenRepository.findById(email)).thenReturn(Optional.of(storedToken));

            // When & Then
            assertThatThrownBy(() -> tokenService.reissue(clientRefreshToken))
                    .isInstanceOf(CoreException.class)
                    .satisfies(ex -> assertThat(((CoreException) ex).getErrorType())
                            .isEqualTo(ErrorType.TOKEN_MISMATCH));
            verify(tokenRepository).delete(storedToken);
        }
    }

    // ──────────────────────────────────────────────────────────────
    // logout
    // ──────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("logout - 로그아웃")
    class LogoutTest {

        @Test
        @DisplayName("성공 - 유효한 AccessToken이면 Redis 블랙리스트에 등록된다")
        void success_blacklistRegistered() {
            // Given
            String email = "user@example.com";
            String accessToken = "valid-access-token";
            JwtPayloadInfo payloadInfo = JwtPayloadInfo.of("jti-uuid", 180L);
            ValueOperations<String, String> valueOps = mock(ValueOperations.class);

            when(jwtTokenProvider.getPayloadInfo(accessToken)).thenReturn(payloadInfo);
            when(redisTemplate.opsForValue()).thenReturn(valueOps);

            // When
            tokenService.logout(email, accessToken);

            // Then
            verify(tokenRepository).deleteById(email);
            verify(valueOps).set(eq("bl:access:jti-uuid"), eq("logout"), eq(180L), eq(TimeUnit.SECONDS));
        }

        @Test
        @DisplayName("성공 - remainingTime이 0 이하이면 블랙리스트 등록을 생략한다")
        void success_expiredToken_skipsBlacklist() {
            // Given
            String email = "user@example.com";
            String accessToken = "expired-access-token";
            JwtPayloadInfo payloadInfo = JwtPayloadInfo.of("jti-uuid", 0L);

            when(jwtTokenProvider.getPayloadInfo(accessToken)).thenReturn(payloadInfo);

            // When
            tokenService.logout(email, accessToken);

            // Then
            verify(tokenRepository).deleteById(email);
            verify(redisTemplate, never()).opsForValue();
        }

        @Test
        @DisplayName("성공 - jti가 null이면 블랙리스트 등록을 생략한다")
        void success_nullJti_skipsBlacklist() {
            // Given
            String email = "user@example.com";
            String accessToken = "access-token-no-jti";
            JwtPayloadInfo payloadInfo = JwtPayloadInfo.of(null, 180L);

            when(jwtTokenProvider.getPayloadInfo(accessToken)).thenReturn(payloadInfo);

            // When
            tokenService.logout(email, accessToken);

            // Then
            verify(tokenRepository).deleteById(email);
            verify(redisTemplate, never()).opsForValue();
        }
    }
}
