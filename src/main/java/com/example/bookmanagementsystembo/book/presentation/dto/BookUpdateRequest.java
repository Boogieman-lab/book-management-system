package com.example.bookmanagementsystembo.book.presentation.dto;

import com.example.bookmanagementsystembo.book.domain.dto.BookUpdateDto;

import java.util.List;

public record BookUpdateRequest(
        String title,
        List<String> authors,
        String contents,
        String url,
        List<String> translators,
        String publisher,
        Integer price,
        Integer salePrice,
        String thumbnail,
        String status
) {
    public BookUpdateDto toCommand(Long bookId) {
        return new BookUpdateDto(
                bookId,
                title,
                authors,
                contents,
                url,
                translators,
                publisher,
                price,
                salePrice,
                thumbnail,
                status
        );
    }
}
