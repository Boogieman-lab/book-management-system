package com.example.bookmanagementsystembo.token.presentation.controller;


import com.example.bookmanagementsystembo.token.domain.dto.CreateTokenDto;
import com.example.bookmanagementsystembo.token.domain.service.TokenService;
import com.example.bookmanagementsystembo.token.presentation.dto.TokenIssueReq;
import com.example.bookmanagementsystembo.token.presentation.dto.TokenLogoutReq;
import com.example.bookmanagementsystembo.token.presentation.dto.TokenReIssueReq;
import com.example.bookmanagementsystembo.token.presentation.dto.TokenRes;
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

//    @PostMapping("/issue")
//    public ResponseEntity<TokenRes> issueToken(@RequestBody TokenIssueReq req) {
//        CreateTokenDto createToken = tokenService.issue(req.userEmail());
//        return ResponseEntity.ok(TokenRes.from(createToken));
//    }

    @PostMapping("/reissue")
    public ResponseEntity<TokenRes> reissueToken(@RequestBody TokenReIssueReq req) {
        CreateTokenDto response = tokenService.reissue(req.refreshToken());
        return ResponseEntity.ok(TokenRes.from(response));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logoutToken(@RequestBody TokenLogoutReq req) {
        tokenService.logout(req.userEmail(), req.accessToken());
        return ResponseEntity.noContent().build();
    }
}
