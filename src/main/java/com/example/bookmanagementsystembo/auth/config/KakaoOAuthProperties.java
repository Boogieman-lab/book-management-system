package com.example.bookmanagementsystembo.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "kakao")
public record KakaoOAuthProperties(String clientId, String redirectUri, String tokenUrl, String userInfoUrl) {

}
