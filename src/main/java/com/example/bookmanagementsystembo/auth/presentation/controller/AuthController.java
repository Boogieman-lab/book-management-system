package com.example.bookmanagementsystembo.auth.presentation.controller;

import com.example.bookmanagementsystembo.auth.domain.service.AuthService;
import com.example.bookmanagementsystembo.auth.presentation.dto.LoginRequest;
import com.example.bookmanagementsystembo.auth.presentation.dto.LoginResponse;
import com.example.bookmanagementsystembo.auth.presentation.dto.SignupRequest;
import com.example.bookmanagementsystembo.token.dto.TokenResponse;
import com.example.bookmanagementsystembo.token.dto.TokenReissueRequest;
import com.example.bookmanagementsystembo.token.service.TokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/auth")
@RequiredArgsConstructor
@RestController
public class AuthController {

    private final AuthService authService;
    private final TokenService tokenService;

    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@RequestBody @Valid SignupRequest request) {
        authService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody @Valid LoginRequest request,
            HttpServletResponse response) {
        LoginResponse loginResponse = authService.login(request);

        // accessToken을 HttpOnly 쿠키에 설정
        // 브라우저가 이후 모든 요청(페이지 포함)에 자동 전송 → JwtAuthenticationFilter가 쿠키에서 읽어 SecurityContext 구성
        // → Thymeleaf sec:authorize 표현식이 서버 렌더링 시점에 올바르게 평가됨
        Cookie accessTokenCookie = new Cookie("accessToken", loginResponse.accessToken());
        accessTokenCookie.setHttpOnly(true);  // JS에서 접근 불가 → XSS 방어
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(180);     // AccessToken 만료시간과 동일 (180초)
        // accessTokenCookie.setSecure(true); // HTTPS 환경 배포 시 활성화
        response.addCookie(accessTokenCookie);

        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) {
        String accessToken = extractTokenFromRequest(request);
        if (accessToken != null && authentication != null) {
            tokenService.logout(authentication.getName(), accessToken);
        }

        // accessToken 쿠키 만료 처리
        Cookie expiredCookie = new Cookie("accessToken", "");
        expiredCookie.setHttpOnly(true);
        expiredCookie.setPath("/");
        expiredCookie.setMaxAge(0);
        response.addCookie(expiredCookie);

        return ResponseEntity.noContent().build();
    }

    /** Authorization 헤더(Bearer) 또는 쿠키에서 accessToken을 추출합니다. */
    private String extractTokenFromRequest(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            return header.substring(7);
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

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@RequestBody @Valid TokenReissueRequest request) {
        return ResponseEntity.ok(tokenService.reissue(request.refreshToken()));
    }
}
