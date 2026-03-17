package com.example.bookmanagementsystembo.auth.presentation.dto;

public record LoginResponse(
        String accessToken,
        String refreshToken,
        String email,
        String name
) {}
