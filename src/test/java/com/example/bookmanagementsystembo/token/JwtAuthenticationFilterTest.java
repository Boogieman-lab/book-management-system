package com.example.bookmanagementsystembo.token;

import com.example.bookmanagementsystembo.token.config.JwtAuthenticationFilter;
import com.example.bookmanagementsystembo.token.config.JwtTokenProvider;
import com.example.bookmanagementsystembo.user.dto.CustomUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("JwtAuthenticationFilter 통합 테스트")
@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @InjectMocks
    private JwtAuthenticationFilter filter;

    @Mock
    private FilterChain filterChain;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    // ──────────────────────────────────────────────────────────────
    // 유효한 토큰 — SecurityContext 등록
    // ──────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("유효한 Access Token")
    class ValidTokenTest {

        private static final String VALID_TOKEN = "valid.jwt.token";

        @BeforeEach
        void stubValidToken() {
            when(tokenProvider.isInvalid(VALID_TOKEN)).thenReturn(false);
            when(tokenProvider.getJti(VALID_TOKEN)).thenReturn("jti-uuid-001");
            when(redisTemplate.hasKey("bl:access:jti-uuid-001")).thenReturn(false);
            when(tokenProvider.getUsername(VALID_TOKEN)).thenReturn("user@example.com");
            when(tokenProvider.getRole(VALID_TOKEN)).thenReturn("ROLE_USER");
            when(tokenProvider.getUserId(VALID_TOKEN)).thenReturn(99L);
        }

        @Test
        @DisplayName("Authorization 헤더 Bearer 토큰으로 SecurityContext에 CustomUserDetails가 등록된다")
        void bearerHeader_loadsSecurityContext() throws Exception {
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.addHeader("Authorization", "Bearer " + VALID_TOKEN);
            MockHttpServletResponse response = new MockHttpServletResponse();

            filter.doFilter(request, response, filterChain);

            var auth = SecurityContextHolder.getContext().getAuthentication();
            assertThat(auth).isNotNull();
            assertThat(auth.getPrincipal()).isInstanceOf(CustomUserDetails.class);

            CustomUserDetails principal = (CustomUserDetails) auth.getPrincipal();
            assertThat(principal.getUserId()).isEqualTo(99L);
            assertThat(principal.getUsername()).isEqualTo("user@example.com");
        }

        @Test
        @DisplayName("(중요) DB 조회 없이 토큰만으로 SecurityContext에 CustomUserDetails가 로드된다")
        void loadsSecurityContext_withoutDbLookup() throws Exception {
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.addHeader("Authorization", "Bearer " + VALID_TOKEN);
            MockHttpServletResponse response = new MockHttpServletResponse();

            filter.doFilter(request, response, filterChain);

            // tokenProvider의 3가지 클레임 추출 메서드만 호출돼야 함
            verify(tokenProvider).getUsername(VALID_TOKEN);
            verify(tokenProvider).getRole(VALID_TOKEN);
            verify(tokenProvider).getUserId(VALID_TOKEN);

            // 필터 체인은 정상 통과
            verify(filterChain).doFilter(request, response);
        }

        @Test
        @DisplayName("쿠키(accessToken)로도 SecurityContext에 인증 정보가 등록된다")
        void cookieToken_loadsSecurityContext() throws Exception {
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setCookies(new Cookie("accessToken", VALID_TOKEN));
            MockHttpServletResponse response = new MockHttpServletResponse();

            filter.doFilter(request, response, filterChain);

            var auth = SecurityContextHolder.getContext().getAuthentication();
            assertThat(auth).isNotNull();

            CustomUserDetails principal = (CustomUserDetails) auth.getPrincipal();
            assertThat(principal.getUserId()).isEqualTo(99L);
        }

        @Test
        @DisplayName("Authorization 헤더가 쿠키보다 우선 처리된다")
        void bearerHeaderTakesPriorityOverCookie() throws Exception {
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.addHeader("Authorization", "Bearer " + VALID_TOKEN);
            request.setCookies(new Cookie("accessToken", "cookie.token.value"));
            MockHttpServletResponse response = new MockHttpServletResponse();

            filter.doFilter(request, response, filterChain);

            // Bearer 토큰으로 검증됨 (쿠키 토큰으로는 호출 안 됨)
            verify(tokenProvider).isInvalid(VALID_TOKEN);
            verify(tokenProvider, never()).isInvalid("cookie.token.value");
        }
    }

    // ──────────────────────────────────────────────────────────────
    // 토큰 없음
    // ──────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("토큰 없음")
    class NoTokenTest {

        @Test
        @DisplayName("토큰이 없으면 SecurityContext가 비어있고 필터 체인을 통과한다")
        void noToken_passesThroughChain() throws Exception {
            MockHttpServletRequest request = new MockHttpServletRequest();
            MockHttpServletResponse response = new MockHttpServletResponse();

            filter.doFilter(request, response, filterChain);

            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
            verify(filterChain).doFilter(request, response);
            verify(tokenProvider, never()).isInvalid(any());
        }
    }

    // ──────────────────────────────────────────────────────────────
    // 블랙리스트 토큰 (로그아웃된 토큰)
    // ──────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("블랙리스트 토큰")
    class BlacklistedTokenTest {

        private static final String BLACKLISTED_TOKEN = "blacklisted.jwt.token";

        @Test
        @DisplayName("로그아웃된 토큰(블랙리스트)은 401 응답을 반환하고 필터 체인을 중단한다")
        void blacklistedToken_returns401() throws Exception {
            when(tokenProvider.isInvalid(BLACKLISTED_TOKEN)).thenReturn(false);
            when(tokenProvider.getJti(BLACKLISTED_TOKEN)).thenReturn("jti-blacklisted");
            when(redisTemplate.hasKey("bl:access:jti-blacklisted")).thenReturn(true);

            MockHttpServletRequest request = new MockHttpServletRequest();
            request.addHeader("Authorization", "Bearer " + BLACKLISTED_TOKEN);
            MockHttpServletResponse response = new MockHttpServletResponse();

            filter.doFilter(request, response, filterChain);

            assertThat(response.getStatus()).isEqualTo(401);
            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
            verify(filterChain, never()).doFilter(any(), any());
        }
    }

    // ──────────────────────────────────────────────────────────────
    // 유효하지 않은 토큰 (서명 오류, 만료 등)
    // ──────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("유효하지 않은 토큰")
    class InvalidTokenTest {

        @Test
        @DisplayName("서명이 잘못된 토큰은 401 응답을 반환하고 필터 체인을 중단한다")
        void invalidToken_returns401() throws Exception {
            when(tokenProvider.isInvalid("tampered.jwt.token")).thenReturn(true);

            MockHttpServletRequest request = new MockHttpServletRequest();
            request.addHeader("Authorization", "Bearer tampered.jwt.token");
            MockHttpServletResponse response = new MockHttpServletResponse();

            filter.doFilter(request, response, filterChain);

            assertThat(response.getStatus()).isEqualTo(401);
            verify(filterChain, never()).doFilter(any(), any());
        }

        @Test
        @DisplayName("JTI가 없는 토큰은 401 응답을 반환하고 필터 체인을 중단한다")
        void missingJti_returns401() throws Exception {
            when(tokenProvider.isInvalid("no.jti.token")).thenReturn(false);
            when(tokenProvider.getJti("no.jti.token")).thenReturn(""); // 빈 JTI

            MockHttpServletRequest request = new MockHttpServletRequest();
            request.addHeader("Authorization", "Bearer no.jti.token");
            MockHttpServletResponse response = new MockHttpServletResponse();

            filter.doFilter(request, response, filterChain);

            assertThat(response.getStatus()).isEqualTo(401);
            verify(filterChain, never()).doFilter(any(), any());
        }
    }
}
