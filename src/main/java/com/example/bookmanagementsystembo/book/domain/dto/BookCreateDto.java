package com.example.bookmanagementsystembo.book.domain.dto;

import java.time.LocalDateTime;
import java.util.List;

public record BookCreateDto(
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
}
