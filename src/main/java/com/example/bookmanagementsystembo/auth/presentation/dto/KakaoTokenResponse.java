package com.example.bookmanagementsystembo.auth.presentation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class KakaoTokenResponse {
    @JsonProperty("token_type")
    public String tokenType;                    // 필수 토큰 타입
    @JsonProperty("access_token")
    public String accessToken;                  // 필수 사용자 액세스 토큰 값
    @JsonProperty("id_token")
    public String idToken;                      //     ID 토큰 값
    @JsonProperty("expires_in")
    public Integer expiresIn;                   // 필수 액세스 토큰과 ID 토큰의 만료 시간(초)
    @JsonProperty("refresh_token")
    public String refreshToken;                 // 필수 사용자 리프레시 토큰 값
    @JsonProperty("refresh_token_expires_in")
    public Integer refreshTokenExpiresIn;       // 필수 리프레시 토큰 만료 시간(초)
    @JsonProperty("scope")
    public String scope;                        // 인증된 사용자의 정보 조회 권한 범위
}
