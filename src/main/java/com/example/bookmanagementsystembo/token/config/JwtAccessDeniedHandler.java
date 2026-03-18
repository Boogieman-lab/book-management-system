package com.example.bookmanagementsystembo.token.config;

import com.example.bookmanagementsystembo.exception.ErrorCode;
import com.example.bookmanagementsystembo.exception.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 인증은 됐지만 권한이 부족한 요청에 대해 호출됩니다.
 * Spring Security 기본 동작(403 페이지) 대신 JSON 403 응답을 반환합니다.
 *
 * 발생 시나리오:
 * - ROLE_USER가 /api/v1/admin/** 접근 시도
 * - ROLE_ADMIN이 아닌 사용자가 관리자 전용 API 호출
 */
@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        ErrorResponse body = ErrorResponse.of(
                ErrorCode.FORBIDDEN.getMessage(),
                "접근 권한이 없습니다. 관리자 권한(ROLE_ADMIN)이 필요합니다."
        );
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
