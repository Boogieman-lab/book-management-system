package com.example.bookmanagementsystembo.token.controller;


import com.example.bookmanagementsystembo.token.service.TokenService;
import com.example.bookmanagementsystembo.token.dto.TokenLogoutRequest;
import com.example.bookmanagementsystembo.token.dto.TokenReissueRequest;
import com.example.bookmanagementsystembo.token.dto.TokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/token/")
public class TokenController {

    private final TokenService tokenService;


    @PostMapping("/reissue")
    public ResponseEntity<TokenResponse> reissueToken(@RequestBody TokenReissueRequest req) {
        return ResponseEntity.ok(tokenService.reissue(req.refreshToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logoutToken(@RequestBody TokenLogoutRequest req) {
        tokenService.logout(req.userEmail(), req.accessToken());
        return ResponseEntity.noContent().build();
    }
}
