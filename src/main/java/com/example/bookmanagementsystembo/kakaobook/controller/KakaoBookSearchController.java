package com.example.bookmanagementsystembo.kakaobook.controller;

import com.example.bookmanagementsystembo.kakaobook.dto.KakaoBookSearchRes;
import com.example.bookmanagementsystembo.kakaobook.service.KakaoBookSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/search/books")
@RequiredArgsConstructor
@RestController
public class KakaoBookSearchController {

    private final KakaoBookSearchService kakaoBookSearchService;

    /**
     * 제목으로 책 검색
     */
    @GetMapping
    public ResponseEntity<KakaoBookSearchRes> getBooksByTitle(@RequestParam String title) {
        return ResponseEntity.ok(kakaoBookSearchService.getBooksByTitle(title));
    }

}
