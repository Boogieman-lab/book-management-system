package com.example.bookmanagementsystembo.book.domain.dto;

import java.util.List;

public record BookUpdateDto(
        Long bookId,
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
) {}
