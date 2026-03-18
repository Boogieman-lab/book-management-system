package com.example.bookmanagementsystembo.auth.config;

import com.example.bookmanagementsystembo.token.config.JwtAccessDeniedHandler;
import com.example.bookmanagementsystembo.token.config.JwtAuthenticationEntryPoint;
import com.example.bookmanagementsystembo.token.config.JwtAuthenticationFilter;
import com.example.bookmanagementsystembo.token.config.JwtProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 설정.
 *
 * [인증/인가 규칙]
 * - 공개 API   : /api/auth/**, /api/v1/auth/**, /api/oauth/**, Swagger, 정적 리소스
 * - 관리자 전용 : /api/v1/admin/**, /api/admin/**  → ROLE_ADMIN 필수
 * - 일반 사용자 : 그 외 /api/v1/**, /api/**       → ROLE_USER 또는 ROLE_ADMIN
 *
 * [보안 예외 처리]
 * - 미인증 접근 → JwtAuthenticationEntryPoint (JSON 401)
 * - 권한 부족   → JwtAccessDeniedHandler       (JSON 403)
 *
 * [세션 정책] STATELESS — JWT 기반이므로 서버 세션 미사용
 */
@EnableConfigurationProperties({JwtProperties.class})
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth

                        // ── 정적 리소스 (Thymeleaf 레이아웃용) ──────────────────────────
                        .requestMatchers("/", "/css/**", "/js/**", "/images/**", "/favicon.ico").permitAll()

                        // ── Swagger / OpenAPI ──────────────────────────────────────────
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui.html",
                                "/swagger-ui/**"
                        ).permitAll()

                        // ── 공개 인증 API (로그인·회원가입·토큰 갱신) ──────────────────
                        // /api/auth/** : 기존 구현 경로
                        // /api/v1/auth/**: API 명세 기준 경로 (향후 버전 통합 예정)
                        .requestMatchers("/api/auth/**", "/api/v1/auth/**").permitAll()

                        // ── Kakao OAuth 콜백 (공개) ────────────────────────────────────
                        .requestMatchers("/api/oauth/**").permitAll()

                        // ── 관리자 전용 API (ROLE_ADMIN 필수) ─────────────────────────
                        .requestMatchers("/api/v1/admin/**", "/api/admin/**")
                                .hasAuthority("ROLE_ADMIN")

                        // ── UI 개발 임시 허용 (UI 완성 후 제거 예정) ──────────────────
                        // TODO: UI 개발 완료 후 아래 두 줄 삭제 후 적절한 권한 설정 적용
                        .requestMatchers("/user/**", "/book/**").permitAll()

                        // ── 나머지 모든 요청: ROLE_USER 또는 ROLE_ADMIN ───────────────
                        .anyRequest().hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
                )

                // 인증/인가 실패 시 JSON 응답 처리
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)  // 401
                        .accessDeniedHandler(jwtAccessDeniedHandler)            // 403
                )

                // JWT 필터를 UsernamePasswordAuthenticationFilter 앞에 등록
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
