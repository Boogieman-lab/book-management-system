package com.example.bookmanagementsystembo.book.dto;

import com.example.bookmanagementsystembo.book.entity.Book;
import com.example.bookmanagementsystembo.book.utils.JsonUtils;

import java.util.List;

/**
 * 도서 목록 조회 응답 (요약 정보 + 재고 수량).
 * GET /api/v1/books 의 page content로 사용됩니다.
 */
public record BookSummaryRes(
        Long bookId,
        String title,
        List<String> authors,
        String publisher,
        String thumbnail,
        String isbn,
        int availableStock,
        int totalStock
) {
    public static BookSummaryRes of(Book book, int availableStock, int totalStock) {
        return new BookSummaryRes(
                book.getBookId(),
                book.getTitle(),
                JsonUtils.toList(book.getAuthors()),
                book.getPublisher(),
                book.getThumbnail(),
                book.getIsbn(),
                availableStock,
                totalStock
        );
    }
}
