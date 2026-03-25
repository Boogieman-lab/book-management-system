package com.example.bookmanagementsystembo.book.dto;

/**
 * ISBN 보유 여부 확인 응답.
 * GET /api/v1/books/isbn-check?isbn={isbn}
 *
 * @param exists     book 테이블에 동일 ISBN 도서가 존재하면 true
 * @param totalStock book_hold 테이블의 해당 도서 실물 총 보유 수량
 */
public record BookIsbnCheckResponse(boolean exists, int totalStock) {}
