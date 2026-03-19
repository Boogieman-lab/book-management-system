package com.example.bookmanagementsystembo.auth.presentation.controller;

import com.example.bookmanagementsystembo.auth.domain.service.AuthService;
import com.example.bookmanagementsystembo.auth.presentation.dto.LoginRequest;
import com.example.bookmanagementsystembo.auth.presentation.dto.LoginResponse;
import com.example.bookmanagementsystembo.auth.presentation.dto.SignupRequest;
import com.example.bookmanagementsystembo.token.dto.TokenResponse;
import com.example.bookmanagementsystembo.token.dto.TokenReissueRequest;
import com.example.bookmanagementsystembo.token.service.TokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/auth")
@RequiredArgsConstructor
@RestController
public class AuthController {

    private final AuthService authService;
    private final TokenService tokenService;

    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@RequestBody @Valid SignupRequest request) {
        authService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestHeader("Authorization") String authHeader,
            Authentication authentication) {
        String accessToken = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
        tokenService.logout(authentication.getName(), accessToken);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@RequestBody @Valid TokenReissueRequest request) {
        return ResponseEntity.ok(tokenService.reissue(request.refreshToken()));
    }
}
