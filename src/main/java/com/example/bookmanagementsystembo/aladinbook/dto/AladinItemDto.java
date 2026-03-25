package com.example.bookmanagementsystembo.aladinbook.dto;

/**
 * 알라딘 API 상품 검색 결과의 개별 item 필드.
 * Output=JS 응답에서 item[] 배열의 각 원소를 매핑합니다.
 */
public record AladinItemDto(
        String title,
        String link,
        String author,
        String pubdate,
        String description,
        String isbn,
        String isbn13,
        int pricesales,
        int pricestandard,
        String cover,
        String publisher,
        int categoryId,
        String categoryName,
        long itemId
) {
}
