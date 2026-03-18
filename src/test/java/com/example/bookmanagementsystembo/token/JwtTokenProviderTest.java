package com.example.bookmanagementsystembo.token;

import com.example.bookmanagementsystembo.token.config.JwtProperties;
import com.example.bookmanagementsystembo.token.config.JwtTokenProvider;
import com.example.bookmanagementsystembo.user.enums.Role;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("JwtTokenProvider 단위 테스트")
class JwtTokenProviderTest {

    // 512비트 이상 HS512 전용 Base64 시크릿
    private static final String TEST_SECRET =
            "DUGJPydQDibKa/c4X/q/60S8xNQw1SD08GVLZJAJyC3X1Sd3IRNlW+vb5EWIkTQWgHykiKpW0T3upDUNLCROow==";

    private JwtTokenProvider tokenProvider;

    @BeforeEach
    void setUp() {
        JwtProperties props = new JwtProperties(TEST_SECRET, 180L, 300L);
        tokenProvider = new JwtTokenProvider(props);
        tokenProvider.init();
    }

    // ──────────────────────────────────────────────────────────────
    // Access Token 생성 및 클레임 검증
    // ──────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("Access Token 생성")
    class CreateAccessTokenTest {

        @Test
        @DisplayName("userId 클레임이 토큰에 포함된다")
        void shouldContainUserId() {
            String token = tokenProvider.createAccessToken("user@example.com", Role.ROLE_USER, 42L);

            Long userId = tokenProvider.getUserId(token);

            assertThat(userId).isEqualTo(42L);
        }

        @Test
        @DisplayName("role 클레임이 토큰에 포함된다")
        void shouldContainRole() {
            String token = tokenProvider.createAccessToken("user@example.com", Role.ROLE_ADMIN, 1L);

            String role = tokenProvider.getRole(token);

            assertThat(role).isEqualTo("ROLE_ADMIN");
        }

        @Test
        @DisplayName("subject(email)가 토큰에 포함된다")
        void shouldContainEmail() {
            String token = tokenProvider.createAccessToken("user@example.com", Role.ROLE_USER, 1L);

            String email = tokenProvider.getUsername(token);

            assertThat(email).isEqualTo("user@example.com");
        }

        @Test
        @DisplayName("JTI(jti)가 존재하며 비어있지 않다")
        void shouldContainNonBlankJti() {
            String token = tokenProvider.createAccessToken("user@example.com", Role.ROLE_USER, 1L);

            String jti = tokenProvider.getJti(token);

            assertThat(jti).isNotBlank();
        }

        @Test
        @DisplayName("두 번 생성한 토큰의 JTI는 서로 다르다")
        void jtiShouldBeUnique() {
            String token1 = tokenProvider.createAccessToken("user@example.com", Role.ROLE_USER, 1L);
            String token2 = tokenProvider.createAccessToken("user@example.com", Role.ROLE_USER, 1L);

            assertThat(tokenProvider.getJti(token1))
                    .isNotEqualTo(tokenProvider.getJti(token2));
        }

        @Test
        @DisplayName("유효한 토큰은 isInvalid() = false를 반환한다")
        void validToken_isNotInvalid() {
            String token = tokenProvider.createAccessToken("user@example.com", Role.ROLE_USER, 1L);

            assertThat(tokenProvider.isInvalid(token)).isFalse();
        }
    }

    // ──────────────────────────────────────────────────────────────
    // 만료 토큰 검증
    // ──────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("만료된 토큰")
    class ExpiredTokenTest {

        private JwtTokenProvider expiredProvider;

        @BeforeEach
        void setUpExpiredProvider() {
            // accessSec = -1 로 즉시 만료 토큰 생성
            JwtProperties props = new JwtProperties(TEST_SECRET, -1L, 300L);
            expiredProvider = new JwtTokenProvider(props);
            expiredProvider.init();
        }

        @Test
        @DisplayName("만료된 Access Token은 isInvalid() = true를 반환한다")
        void expiredToken_isInvalid() {
            String token = expiredProvider.createAccessToken("user@example.com", Role.ROLE_USER, 1L);

            assertThat(expiredProvider.isInvalid(token)).isTrue();
        }

        @Test
        @DisplayName("만료된 토큰으로 클레임 파싱 시 JwtException이 발생한다")
        void expiredToken_throwsOnParse() {
            String expiredToken = expiredProvider.createAccessToken("user@example.com", Role.ROLE_USER, 1L);

            // 정상 provider로 만료 토큰 파싱 시도 → 예외 발생
            assertThatThrownBy(() -> tokenProvider.getUsername(expiredToken))
                    .isInstanceOf(JwtException.class);
        }
    }

    // ──────────────────────────────────────────────────────────────
    // 변조된 토큰 검증
    // ──────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("변조된 토큰")
    class TamperedTokenTest {

        @Test
        @DisplayName("서명이 변조된 토큰은 isInvalid() = true를 반환한다")
        void tamperedSignature_isInvalid() {
            String token = tokenProvider.createAccessToken("user@example.com", Role.ROLE_USER, 1L);
            String tampered = token.substring(0, token.lastIndexOf('.') + 1) + "invalidsignature";

            assertThat(tokenProvider.isInvalid(tampered)).isTrue();
        }

        @Test
        @DisplayName("완전히 잘못된 토큰 문자열은 isInvalid() = true를 반환한다")
        void randomString_isInvalid() {
            assertThat(tokenProvider.isInvalid("not.a.jwt")).isTrue();
        }

        @Test
        @DisplayName("null/빈 토큰은 isInvalid() = true를 반환한다")
        void emptyToken_isInvalid() {
            assertThat(tokenProvider.isInvalid("")).isTrue();
            assertThat(tokenProvider.isInvalid("   ")).isTrue();
        }
    }

    // ──────────────────────────────────────────────────────────────
    // Refresh Token 검증
    // ──────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("Refresh Token 검증")
    class RefreshTokenTest {

        @Test
        @DisplayName("Refresh Token은 isValid() = true를 반환한다")
        void validRefreshToken_isValid() {
            String token = tokenProvider.createRefreshToken("user@example.com");

            assertThat(tokenProvider.isValid(token)).isTrue();
        }

        @Test
        @DisplayName("Refresh Token에서 이메일(subject)을 추출할 수 있다")
        void shouldExtractEmailFromRefreshToken() {
            String token = tokenProvider.createRefreshToken("user@example.com");

            assertThat(tokenProvider.getUsername(token)).isEqualTo("user@example.com");
        }
    }
}
