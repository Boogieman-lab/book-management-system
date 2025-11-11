package com.example.bookmanagementsystembo.auth.presentation.controller;

import com.example.bookmanagementsystembo.auth.domain.service.KakaoLoginService;
import com.example.bookmanagementsystembo.auth.presentation.dto.LoginResult;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URI;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/oauth")
public class KakaoLoginController {

    private final KakaoLoginService kakaoLoginService;

    @GetMapping("/code/kakao/callback")
    public ResponseEntity<Void> kakaoCallback(@RequestParam("code") String code, HttpServletResponse response) throws IOException {
        LoginResult result = kakaoLoginService.login(code);

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", result.refreshToken())
                .httpOnly(true)
                .secure(false)
                .path("/api/auth/refresh")
                .maxAge(60 * 4)
                .sameSite("Lax")
                .build();


        ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", result.accessToken())
                .httpOnly(true)
                .secure(false) // 운영 환경에서는 반드시 true
                .path("/")
                .maxAge(60 * 2) // 1시간
                .sameSite("Lax")
                .build();

        response.addHeader("Set-Cookie", refreshTokenCookie.toString());
        response.addHeader("Set-Cookie", accessTokenCookie.toString());
        return ResponseEntity.status(HttpStatus.FOUND) // 302 상태 코드
                .location(URI.create("/user/book/bookList"))
                .build();
    }
}
