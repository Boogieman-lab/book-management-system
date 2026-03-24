package com.example.bookmanagementsystembo.aladinbook.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 알라딘 검색 결과 페이지네이션 메타 정보.
 * 프론트엔드에서 data.meta.is_end 로 접근합니다.
 */
public record AladinBookMeta(
        @JsonProperty("is_end") boolean isEnd,
        int totalResults
) {
}
