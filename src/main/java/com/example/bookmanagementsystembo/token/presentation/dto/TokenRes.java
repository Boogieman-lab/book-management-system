package com.example.bookmanagementsystembo.token.presentation.dto;

import com.example.bookmanagementsystembo.token.domain.dto.CreateTokenDto;

public record TokenRes(String accessToken, String refreshToken) {
    public static TokenRes from(CreateTokenDto dto) {
        return new TokenRes(dto.accessToken(), dto.refreshToken());
    }
}
