package com.example.bookmanagementsystembo.token.domain.dto;

public record CreateTokenDto(String accessToken, String refreshToken) {

    public static CreateTokenDto of(String accessToken, String refreshToken) {
        return new CreateTokenDto(accessToken, refreshToken);
    }
}
