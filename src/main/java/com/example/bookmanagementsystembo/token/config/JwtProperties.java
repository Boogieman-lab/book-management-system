package com.example.bookmanagementsystembo.token.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(String secret, long accessSec, long refreshSec) {
}