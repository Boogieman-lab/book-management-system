package com.example.bookmanagementsystembo.auth.presentation.controller;

import com.example.bookmanagementsystembo.auth.domain.service.KakaoLoginService;
import com.example.bookmanagementsystembo.auth.presentation.dto.LoginResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/oauth/code/kakao")
public class KakaoLoginController {

    private final KakaoLoginService kakaoLoginService;

    @GetMapping("/callback")
    public ResponseEntity<?> kakaoCallback(@RequestParam("code") String code) {
        LoginResult result = kakaoLoginService.login(code);
        return ResponseEntity.ok(result);
    }
}
