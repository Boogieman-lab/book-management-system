package com.example.bookmanagementsystembo.book.dto;

import com.example.bookmanagementsystembo.book.entity.Book;
import com.example.bookmanagementsystembo.book.utils.JsonUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 도서 상세 조회 응답 (전체 필드 + 재고 수량).
 * GET /api/v1/books/{bookId} 응답으로 사용됩니다.
 */
public record BookDetailResponse(
        Long bookId,
        String title,
        String contents,
        String url,
        String isbn,
        LocalDateTime publishedAt,
        List<String> authors,
        List<String> translators,
        String publisher,
        int price,
        int salePrice,
        String thumbnail,
        int availableStock,
        int totalStock
) {
    public static BookDetailResponse of(Book book, int availableStock, int totalStock) {
        return new BookDetailResponse(
                book.getBookId(),
                book.getTitle(),
                book.getContents(),
                book.getUrl(),
                book.getIsbn(),
                book.getPublishedAt(),
                JsonUtils.toList(book.getAuthors()),
                JsonUtils.toList(book.getTranslators()),
                book.getPublisher(),
                book.getPrice(),
                book.getSalePrice(),
                book.getThumbnail(),
                availableStock,
                totalStock
        );
    }
}
