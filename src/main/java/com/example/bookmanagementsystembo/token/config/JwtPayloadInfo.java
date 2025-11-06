package com.example.bookmanagementsystembo.token.config;

public record JwtPayloadInfo(String jti, long remainingTime) {
    public static JwtPayloadInfo of(String jti, long remainingTime) {
        return new JwtPayloadInfo(jti, remainingTime);
    }
}
