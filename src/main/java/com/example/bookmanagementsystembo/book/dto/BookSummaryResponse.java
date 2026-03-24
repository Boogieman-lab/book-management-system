package com.example.bookmanagementsystembo.book.dto;

import com.example.bookmanagementsystembo.book.entity.Book;

/**
 * 도서 목록 조회 응답 (요약 정보 + 재고 수량).
 * GET /api/v1/books 의 page content로 사용됩니다.
 */
public record BookSummaryResponse(
        Long bookId,
        String title,
        String author,
        String publisher,
        String coverUrl,
        String isbn13,
        String categoryName,
        int priceStandard,
        int availableStock,
        int totalStock
) {
    public static BookSummaryResponse of(Book book, int availableStock, int totalStock) {
        return new BookSummaryResponse(
                book.getBookId(),
                book.getTitle(),
                book.getAuthor(),
                book.getPublisher(),
                book.getCoverUrl(),
                book.getIsbn13(),
                book.getCategoryName(),
                book.getPriceStandard(),
                availableStock,
                totalStock
        );
    }
}
