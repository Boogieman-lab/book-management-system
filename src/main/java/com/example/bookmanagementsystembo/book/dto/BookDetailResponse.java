package com.example.bookmanagementsystembo.book.dto;

import com.example.bookmanagementsystembo.book.entity.Book;

import java.time.LocalDate;

/**
 * 도서 상세 조회 응답 (전체 필드 + 재고 수량).
 * GET /api/v1/books/{bookId} 응답으로 사용됩니다.
 */
public record BookDetailResponse(
        Long bookId,
        String isbn13,
        String isbn10,
        String title,
        String author,
        String publisher,
        LocalDate pubDate,
        String description,
        String coverUrl,
        Integer categoryId,
        String categoryName,
        int priceStandard,
        int priceSales,
        String stockStatus,
        int customerReviewRank,
        Integer seriesId,
        String seriesName,
        String mallType,
        String adultYn,
        int availableStock,
        int totalStock
) {
    public static BookDetailResponse of(Book book, int availableStock, int totalStock) {
        return new BookDetailResponse(
                book.getBookId(),
                book.getIsbn13(),
                book.getIsbn10(),
                book.getTitle(),
                book.getAuthor(),
                book.getPublisher(),
                book.getPubDate(),
                book.getDescription(),
                book.getCoverUrl(),
                book.getCategoryId(),
                book.getCategoryName(),
                book.getPriceStandard(),
                book.getPriceSales(),
                book.getStockStatus(),
                book.getCustomerReviewRank(),
                book.getSeriesId(),
                book.getSeriesName(),
                book.getMallType(),
                book.getAdultYn(),
                availableStock,
                totalStock
        );
    }
}
