package com.example.bookmanagementsystembo.book.dto;

import com.example.bookmanagementsystembo.book.enums.BookSearchField;

/**
 * 도서 검색 조건.
 * keyword가 null/blank이면 전체 조회.
 * field가 null이면 제목+저자+ISBN 통합 검색.
 */
public record BookSearchCond(
        String keyword,
        BookSearchField field
) {}
