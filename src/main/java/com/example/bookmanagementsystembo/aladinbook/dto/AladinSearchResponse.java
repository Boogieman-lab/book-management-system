package com.example.bookmanagementsystembo.aladinbook.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * 알라딘 ItemSearch API 원본 응답 DTO.
 * Output=JS(JSON) 형식의 응답을 그대로 매핑합니다.
 */
public record AladinSearchResponse(
        int totalResults,
        int startIndex,
        int itemsPerPage,
        @JsonProperty("item") List<AladinItemDto> items
) {
}
