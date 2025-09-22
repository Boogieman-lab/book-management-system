package com.example.bookmanagementsystembo.book.presentation.dto;

import com.example.bookmanagementsystembo.book.domain.dto.BookCreateDto;

import java.time.LocalDateTime;
import java.util.List;

public record BookCreateRequest(
        String title,
        String contents,
        String isbn,
        LocalDateTime datetime,
        List<String> authors,
        List<String> translators,
        String publisher,
        int price,
        int salePrice,
        String thumbnail,
        String status,
        String url
) {
    public BookCreateDto toCommand() {
        return new BookCreateDto(title,
                contents,
                isbn,
                datetime,
                authors,
                translators,
                publisher,
                price,
                salePrice,
                thumbnail,
                status,
                url
        );
    }

}
