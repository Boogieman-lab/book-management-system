package com.example.bookmanagementsystembo.security;

import com.example.bookmanagementsystembo.auth.domain.service.AuthService;
import com.example.bookmanagementsystembo.auth.presentation.controller.AuthController;
import com.example.bookmanagementsystembo.auth.config.SecurityConfig;
import com.example.bookmanagementsystembo.token.config.JwtAccessDeniedHandler;
import com.example.bookmanagementsystembo.token.config.JwtAuthenticationEntryPoint;
import com.example.bookmanagementsystembo.token.config.JwtAuthenticationFilter;
import com.example.bookmanagementsystembo.token.config.JwtTokenProvider;
import com.example.bookmanagementsystembo.token.service.TokenService;
import com.example.bookmanagementsystembo.user.dto.CustomUserDetails;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * SecurityFilterChain 인가 규칙 테스트.
 *
 * <p>웹 레이어만 로드(@WebMvcTest)하고, 실제 Security 설정을 @Import로 주입합니다.
 * DB 연결이 필요한 빈(AuthService, TokenService 등)은 @MockBean으로 대체합니다.</p>
 */
@WebMvcTest(AuthController.class)
@Import({
        SecurityConfig.class,
        JwtTokenProvider.class,
        JwtAuthenticationFilter.class,
        JwtAuthenticationEntryPoint.class,
        JwtAccessDeniedHandler.class
})
@TestPropertySource(properties = {
        "jwt.secret=DUGJPydQDibKa/c4X/q/60S8xNQw1SD08GVLZJAJyC3X1Sd3IRNlW+vb5EWIkTQWgHykiKpW0T3upDUNLCROow==",
        "jwt.access-sec=180",
        "jwt.refresh-sec=300"
})
@DisplayName("SecurityFilterChain 인가 테스트")
class SecurityFilterChainTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private TokenService tokenService;

    @MockBean
    private RedisTemplate<String, String> redisTemplate;

    // ──────────────────────────────────────────────────────────────
    // 공개 API — 인증 없이 접근 가능
    // ──────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("공개 API (/api/auth/**)")
    class PublicApiTest {

        @Test
        @DisplayName("인증 없이 /api/auth/login 요청 시 401이 아닌 응답을 받는다")
        void loginEndpoint_isPublic() throws Exception {
            mockMvc.perform(post("/api/auth/login")
                            .contentType("application/json")
                            .content("{\"email\":\"test@test.com\",\"password\":\"pass\"}"))
                    .andExpect(status().is(Matchers.not(401)));
        }

        @Test
        @DisplayName("인증 없이 /api/auth/signup 요청 시 401이 아닌 응답을 받는다")
        void signupEndpoint_isPublic() throws Exception {
            mockMvc.perform(post("/api/auth/signup")
                            .contentType("application/json")
                            .content("{\"name\":\"user\",\"password\":\"pass\",\"email\":\"t@t.com\",\"role\":\"ROLE_USER\"}"))
                    .andExpect(status().is(Matchers.not(401)));
        }
    }

    // ──────────────────────────────────────────────────────────────
    // 보호된 API — 인증 없이 접근 불가
    // ──────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("보호된 API — 미인증 접근")
    class UnauthenticatedAccessTest {

        @Test
        @DisplayName("토큰 없이 보호된 API(/api/v1/users) 요청 시 401을 반환한다")
        void protectedEndpoint_withoutToken_returns401() throws Exception {
            // /api/auth/** 는 permitAll 이므로 실제 보호된 경로를 사용
            mockMvc.perform(get("/api/v1/users"))
                    .andExpect(status().isUnauthorized());
        }
    }

    // ──────────────────────────────────────────────────────────────
    // 관리자 전용 API — ROLE_USER 접근 시 403
    // ──────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("관리자 전용 API (/api/admin/**)")
    class AdminApiTest {

        @Test
        @DisplayName("ROLE_USER로 /api/admin/users 접근 시 403을 반환한다")
        @WithMockUser(roles = "USER")
        void adminEndpoint_withRoleUser_returns403() throws Exception {
            mockMvc.perform(get("/api/admin/users"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("ROLE_ADMIN으로 /api/admin/users 접근 시 403이 아닌 응답을 받는다")
        void adminEndpoint_withRoleAdmin_isAccessible() throws Exception {
            CustomUserDetails admin = new CustomUserDetails(
                    1L, "admin@example.com", "ROLE_ADMIN");

            mockMvc.perform(get("/api/admin/users")
                            .with(user(admin)))
                    .andExpect(status().is(Matchers.not(403)));
        }

        @Test
        @DisplayName("인증 없이 /api/v1/admin/** 접근 시 401을 반환한다")
        void v1AdminEndpoint_withoutAuth_returns401() throws Exception {
            mockMvc.perform(get("/api/v1/admin/users"))
                    .andExpect(status().isUnauthorized());
        }
    }

    // ──────────────────────────────────────────────────────────────
    // 인증된 일반 사용자 — 보호된 API 접근 가능
    // ──────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("인증된 일반 사용자")
    class AuthenticatedUserTest {

        @Test
        @DisplayName("ROLE_USER 인증 후 보호된 API(/api/v1/users) 접근 시 401이 아닌 응답을 받는다")
        void protectedEndpoint_withRoleUser_isAccessible() throws Exception {
            CustomUserDetails userDetails = new CustomUserDetails(
                    10L, "user@example.com", "ROLE_USER");

            // Authorization 헤더를 포함하면 JwtAuthenticationFilter가 가짜 토큰을 검증해 401 반환
            // user() post-processor만 사용하면 필터가 토큰 검증을 건너뛰고 SecurityContext가 유지됨
            mockMvc.perform(get("/api/v1/users")
                            .with(user(userDetails)))
                    .andExpect(status().is(Matchers.not(401)));
        }
    }

    // ──────────────────────────────────────────────────────────────
    // Swagger / 정적 리소스 — 공개
    // ──────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("Swagger 및 정적 리소스")
    class PublicResourceTest {

        @Test
        @DisplayName("인증 없이 /v3/api-docs 접근 시 401이 아닌 응답을 받는다")
        void swaggerApiDocs_isPublic() throws Exception {
            mockMvc.perform(get("/v3/api-docs"))
                    .andExpect(status().is(Matchers.not(401)));
        }

        @Test
        @DisplayName("인증 없이 /swagger-ui.html 접근 시 401이 아닌 응답을 받는다")
        void swaggerUi_isPublic() throws Exception {
            mockMvc.perform(get("/swagger-ui.html"))
                    .andExpect(status().is(Matchers.not(401)));
        }
    }
}
