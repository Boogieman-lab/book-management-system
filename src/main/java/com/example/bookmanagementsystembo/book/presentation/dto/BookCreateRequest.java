package com.example.bookmanagementsystembo.book.presentation.dto;

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
        Integer price,
        Integer salePrice,
        String thumbnail,
        String status,
        String url
) {}
