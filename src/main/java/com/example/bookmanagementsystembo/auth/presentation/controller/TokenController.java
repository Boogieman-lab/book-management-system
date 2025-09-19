package com.example.bookmanagementsystembo.auth.presentation.controller;

import com.example.bookmanagementsystembo.auth.domain.dto.TokenDto;
import com.example.bookmanagementsystembo.auth.domain.service.TokenService;
import com.example.bookmanagementsystembo.auth.presentation.dto.TokenCreateRequest;
import com.example.bookmanagementsystembo.auth.presentation.dto.TokenDeleteRequest;
import com.example.bookmanagementsystembo.auth.presentation.dto.TokenNewCreateRequest;
import com.example.bookmanagementsystembo.auth.presentation.dto.TokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tokens")
public class TokenController {

    private final TokenService tokenService;

    @PostMapping("issue-token")
    public ResponseEntity<TokenResponse> issueToken(@RequestBody TokenCreateRequest request) {
        TokenDto token = tokenService.createToken(request.email(), request.role());
        return ResponseEntity.ok(TokenResponse.of(token.accessToken(), token.refreshToken()));
    }

    @PostMapping("/access")
    public ResponseEntity<TokenResponse> accessToken(@RequestBody TokenNewCreateRequest request) {
        TokenDto token = tokenService.createNewAccessToken(request.refreshToken());
        return ResponseEntity.ok(TokenResponse.of(token.accessToken(), token.refreshToken()));
    }

    @PostMapping("/invalidate")
    public ResponseEntity<Void> invalidateToken(@RequestBody TokenDeleteRequest request) {
        tokenService.deleteByValue(request.refreshToken());
        return ResponseEntity.noContent().build();
    }

}
