package com.example.bookmanagementsystembo.kakaobook.dto;

/**
 * 카카오 도서 검색 API 프록시 요청 파라미터.
 * api_spec.md: GET /api/v1/external/kakao/books
 */
public record KakaoBookSearchParams(
        String query,
        String sort,
        Integer page,
        Integer size,
        String target
) {}
