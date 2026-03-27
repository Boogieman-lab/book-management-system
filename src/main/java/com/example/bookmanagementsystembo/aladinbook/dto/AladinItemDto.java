package com.example.bookmanagementsystembo.aladinbook.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 알라딘 API 상품 검색 결과의 개별 item 필드.
 * Output=JS 응답에서 item[] 배열의 각 원소를 매핑합니다.
 *
 * <p>알라딘 API가 camelCase JSON 키를 반환하므로
 * Jackson @JsonProperty 로 키 이름을 명시적으로 지정합니다.</p>
 */
public record AladinItemDto(
        String title,
        String link,
        String author,
        @JsonProperty("pubDate")        String pubDate,
        String description,
        String isbn,
        String isbn13,
        @JsonProperty("priceSales")     int priceSales,
        @JsonProperty("priceStandard")  int priceStandard,
        String cover,
        String publisher,
        int categoryId,
        String categoryName,
        long itemId,
        String mallType,
        String stockStatus,
        boolean adult,
        int customerReviewRank
) {
}
