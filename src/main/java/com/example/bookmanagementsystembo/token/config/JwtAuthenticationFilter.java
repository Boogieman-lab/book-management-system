package com.example.bookmanagementsystembo.token.config;

import com.example.bookmanagementsystembo.exception.ErrorType;
import com.example.bookmanagementsystembo.exception.ErrorResponse;
import com.example.bookmanagementsystembo.user.dto.CustomUserDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * JWT 인증 필터.
 * 매 요청마다 한 번 실행(OncePerRequestFilter)하여 Access Token을 검증하고
 * SecurityContextHolder에 CustomUserDetails를 등록합니다.
 *
 * 토큰 추출 우선순위: Authorization 헤더(Bearer) > Cookie(accessToken)
 * 검증 순서: 서명 유효성 → JTI 존재 확인 → Redis 블랙리스트 조회
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String BL_ACCESS_PREFIX = "bl:access:";

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain chain)
            throws ServletException, IOException {

        String token = extractToken(request);

        // 토큰이 없으면 인증 없이 통과 (Security 인가 규칙이 접근 제어 담당)
        if (!StringUtils.hasText(token)) {
            chain.doFilter(request, response);
            return;
        }

        // 1. 서명 및 만료 검증
        if (tokenProvider.isInvalid(token)) {
            writeError(response, ErrorType.TOKEN_INVALID);
            return;
        }

        // 2. JTI 존재 확인
        String jti = tokenProvider.getJti(token);
        if (!StringUtils.hasText(jti)) {
            writeError(response, ErrorType.TOKEN_JTI_MISSING);
            return;
        }

        // 3. 로그아웃 블랙리스트 확인 (Redis: bl:access:{jti})
        Boolean blacklisted = redisTemplate.hasKey(BL_ACCESS_PREFIX + jti);
        if (Boolean.TRUE.equals(blacklisted)) {
            writeError(response, ErrorType.TOKEN_BLACKLISTED);
            return;
        }

        // 4. 토큰 claims에서 principal 구성 — DB 조회 없이 처리
        String email  = tokenProvider.getUsername(token);
        String role   = tokenProvider.getRole(token);
        Long   userId = tokenProvider.getUserId(token);

        // CustomUserDetails를 principal로 등록 → 컨트롤러에서 @AuthenticationPrincipal로 접근 가능
        CustomUserDetails principal = new CustomUserDetails(userId, email, role);
        var auth = new UsernamePasswordAuthenticationToken(
                principal, null, principal.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(auth);
        chain.doFilter(request, response);
    }

    /** Authorization 헤더(Bearer) 또는 쿠키(accessToken)에서 토큰을 추출합니다. */
    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            return header.substring(7).trim();
        }
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("accessToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    /** 토큰 오류 시 JSON 형태의 에러 응답을 직접 씁니다. */
    private void writeError(HttpServletResponse response, ErrorType errorType) throws IOException {
        response.setStatus(errorType.getCode().getStatus().value());
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        ErrorResponse body = ErrorResponse.of(
                errorType.getCode().getMessage(), errorType.getMessage());
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
