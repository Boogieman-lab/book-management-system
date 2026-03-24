package com.example.bookmanagementsystembo.aladinbook.dto;

import java.util.List;

/**
 * 알라딘 도서 검색 프록시 API 최종 응답.
 * 프론트엔드가 data.documents 와 data.meta.is_end 로 접근합니다.
 */
public record AladinBookSearchResponse(
        List<AladinBookDocument> documents,
        AladinBookMeta meta
) {
}
