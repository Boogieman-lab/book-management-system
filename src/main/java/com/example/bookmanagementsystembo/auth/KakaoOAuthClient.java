package com.example.bookmanagementsystembo.auth;

import com.example.bookmanagementsystembo.auth.config.KakaoOAuthProperties;
import com.example.bookmanagementsystembo.auth.presentation.dto.KakaoTokenResponse;
import com.example.bookmanagementsystembo.auth.presentation.dto.KakaoUserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class KakaoOAuthClient {
    private final KakaoOAuthProperties props;
    private final RestClient restClient;

    public KakaoTokenResponse getToken(String authorizationCode) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", props.clientId());
        body.add("redirect_uri", props.redirectUri());
        body.add("code", authorizationCode);

        return restClient.post().uri(props.tokenUrl())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(body)
                .retrieve()
                .body(KakaoTokenResponse.class);

    }

    public KakaoUserResponse getUserInfo(String accessToken) {
        return restClient.get()
                .uri(props.userInfoUrl())
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .body(KakaoUserResponse.class);
    }
}
