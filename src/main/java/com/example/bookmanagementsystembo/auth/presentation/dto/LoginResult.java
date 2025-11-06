package com.example.bookmanagementsystembo.auth.presentation.dto;

public record LoginResult(
        String accessToken,
        String refreshToken,
        String name,
        String email
) {

    public static LoginResult of( String accessToken, String refreshToken, String name, String email) {
        return new LoginResult(accessToken, refreshToken, name, email);
    }
}
