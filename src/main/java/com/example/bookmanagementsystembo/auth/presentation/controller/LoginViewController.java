package com.example.bookmanagementsystembo.auth.presentation.controller;

import org.springframework.ui.Model;
import com.example.bookmanagementsystembo.auth.config.KakaoOAuthProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.util.UriComponentsBuilder;

@Controller
@RequestMapping("/user/auth")
@RequiredArgsConstructor
public class LoginViewController {

    private final KakaoOAuthProperties kakaoOAuthProperties;

    @GetMapping("/login")
    public String login(Model model) {

        String url = UriComponentsBuilder.fromHttpUrl("https://kauth.kakao.com/oauth/authorize")
                .queryParam("response_type", "code")
                .queryParam("client_id", kakaoOAuthProperties.clientId())
                .queryParam("redirect_uri", kakaoOAuthProperties.redirectUri())
                .build()
                .toUriString();

        model.addAttribute("url", url);

        return "user/auth/login";
    }
}
