package com.example.bookmanagementsystembo.book.dto;

/**
 * book_hold 배치 집계 결과 DTO.
 * 도서 목록 조회 시 N+1 방지를 위해 GROUP BY 단일 쿼리로 수집됩니다.
 */
public record BookHoldCountDto(long total, long available) {
    public static final BookHoldCountDto EMPTY = new BookHoldCountDto(0, 0);
}
