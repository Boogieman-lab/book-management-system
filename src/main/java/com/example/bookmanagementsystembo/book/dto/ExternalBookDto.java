package com.example.bookmanagementsystembo.book.dto;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

public record ExternalBookDto(
        List<String> authors,
        String contents,
        LocalDateTime datetime,
        String isbn,
        int price,
        String publisher,
        int salePrice,
        String status,
        String thumbnail,
        String title,
        List<String> translators,
        String url
) {
}
