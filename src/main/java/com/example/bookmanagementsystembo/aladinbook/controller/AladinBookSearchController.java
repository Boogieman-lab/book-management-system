package com.example.bookmanagementsystembo.aladinbook.controller;

import com.example.bookmanagementsystembo.aladinbook.dto.AladinBookSearchParams;
import com.example.bookmanagementsystembo.aladinbook.dto.AladinBookSearchResponse;
import com.example.bookmanagementsystembo.aladinbook.service.AladinBookSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 알라딘 도서 검색 프록시 API.
 * Base URL: /api/v1/external/aladin
 *
 * <p>GET /api/v1/external/aladin/books — 알라딘 ItemSearch 프록시 (제목/저자/출판사/통합 검색)
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/external/aladin")
public class AladinBookSearchController {

    private final AladinBookSearchService aladinBookSearchService;

    /**
     * 알라딘 도서 검색.
     *
     * @param query  검색어 (필수)
     * @param target 검색 필드: title | author | publisher | keyword (기본값)
     * @param page   1-based 페이지 번호 (기본값 1, 최대 4 = 총 200건)
     * @param size   페이지당 결과 수 1~50 (기본값 10)
     */
    @GetMapping("/books")
    public ResponseEntity<AladinBookSearchResponse> searchBooks(
            @RequestParam String query,
            @RequestParam(required = false) String target,
            @RequestParam(defaultValue = "1")  Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        AladinBookSearchParams params = new AladinBookSearchParams(query, target, page, size);
        return ResponseEntity.ok(aladinBookSearchService.search(params));
    }
}
