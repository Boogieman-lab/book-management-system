package com.example.bookmanagementsystembo.aladinbook.dto;

import java.util.List;

/**
 * 프론트엔드 전달용 정규화된 도서 도큐먼트.
 *
 * <ul>
 *   <li>isbn  : isbn13 (13자리 숫자)</li>
 *   <li>authors: 저자 목록 (역할 표기 제거)</li>
 *   <li>thumbnail: /cover500/ 고해상도 표지 URL</li>
 *   <li>datetime: 출판일 (YYYYMMDD 형식)</li>
 * </ul>
 */
public record AladinBookDocument(
        String title,
        List<String> authors,
        String publisher,
        String datetime,
        String thumbnail,
        String isbn,
        String contents,
        String category
) {
}
