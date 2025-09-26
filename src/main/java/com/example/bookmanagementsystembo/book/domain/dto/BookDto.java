package com.example.bookmanagementsystembo.book.domain.dto;

import com.example.bookmanagementsystembo.book.domain.Utils;
import com.example.bookmanagementsystembo.book.domain.entity.Book;

import java.time.LocalDateTime;
import java.util.List;


public record BookDto(
        Long bookId,
        List<String> authors,
        List<String> translators,
        String contents,
        LocalDateTime datetime,
        String isbn,
        int price,
        int salePrice,
        String publisher,
        String status,
        String thumbnail,
        String title,
        String url
) {

    public static BookDto from(Book book) {
        return new BookDto(
                null,
                Utils.toList(book.getAuthors()),
                Utils.toList(book.getTranslators()),
                book.getContents(),
                book.getDatetime(),
                book.getIsbn(),
                book.getPrice(),
                book.getSalePrice(),
                book.getPublisher(),
                book.getStatus(),
                book.getThumbnail(),
                book.getTitle(),
                book.getUrl()
        );
    }
}
