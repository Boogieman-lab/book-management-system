package com.example.bookmanagementsystembo.kakaobook.controller;

import com.example.bookmanagementsystembo.kakaobook.dto.KakaoBookSearchParams;
import com.example.bookmanagementsystembo.kakaobook.dto.KakaoBookSearchResponse;
import com.example.bookmanagementsystembo.kakaobook.service.KakaoBookSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class KakaoBookSearchController {

    private final KakaoBookSearchService kakaoBookSearchService;

    /**
     * V1 카카오 도서 검색 프록시 API.
     * GET /api/v1/external/kakao/books
     *
     * @param query  검색 질의어 (필수)
     * @param sort   정렬: accuracy(정확도순, 기본값) | latest(발간일순)
     * @param page   페이지 번호 1~50 (기본 1)
     * @param size   결과 수 1~50 (기본 10)
     * @param target 검색 필드 제한: title | isbn | publisher | person
     */
    @GetMapping("/api/v1/external/kakao/books")
    public ResponseEntity<KakaoBookSearchResponse> searchKakaoBooks(
            @RequestParam String query,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String target
    ) {
        KakaoBookSearchParams params = new KakaoBookSearchParams(query, sort, page, size, target);
        return ResponseEntity.ok(kakaoBookSearchService.search(params));
    }

    /** 기존 엔드포인트 (하위 호환 유지) */
    @GetMapping("/api/search/books")
    public ResponseEntity<KakaoBookSearchResponse> getBooksByTitle(@RequestParam String title) {
        return ResponseEntity.ok(kakaoBookSearchService.getBooksByTitle(title));
    }
}
