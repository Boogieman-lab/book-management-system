package com.example.bookmanagementsystembo.token.config;

import com.example.bookmanagementsystembo.exception.ErrorCode;
import com.example.bookmanagementsystembo.exception.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 인증되지 않은 요청이 보호된 리소스에 접근할 때 호출됩니다.
 * Spring Security 기본 동작(리다이렉트) 대신 JSON 401 응답을 반환합니다.
 *
 * 발생 시나리오:
 * - Authorization 헤더 없이 인증 필요 API 호출
 * - 유효하지 않은 토큰 형식 (JwtAuthenticationFilter를 통과하지 못한 경우)
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        ErrorResponse body = ErrorResponse.of(
                ErrorCode.UNAUTHORIZED.getMessage(),
                "인증이 필요합니다. Access Token을 Authorization 헤더에 포함해주세요."
        );
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
