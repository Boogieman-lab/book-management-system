package com.example.bookmanagementsystembo.book.dto;

import com.example.bookmanagementsystembo.book.entity.Book;

import java.time.LocalDate;
import java.util.List;

public record BookResponse(
        Long bookId,
        String isbn13,
        String isbn10,
        String title,
        String author,
        String publisher,
        LocalDate pubDate,
        String description,
        String coverUrl,
        int priceStandard,
        int priceSales,
        String stockStatus
) {

    public static BookResponse from(Book entity) {
        return new BookResponse(
                entity.getBookId(),
                entity.getIsbn13(),
                entity.getIsbn10(),
                entity.getTitle(),
                entity.getAuthor(),
                entity.getPublisher(),
                entity.getPubDate(),
                entity.getDescription(),
                entity.getCoverUrl(),
                entity.getPriceStandard(),
                entity.getPriceSales(),
                entity.getStockStatus()
        );
    }

    public static List<BookResponse> from(List<Book> books) {
        return books.stream()
                .map(BookResponse::from)
                .toList();
    }
}
