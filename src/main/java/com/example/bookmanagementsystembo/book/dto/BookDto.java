package com.example.bookmanagementsystembo.book.dto;

import java.util.List;

public record BookDto(
        List<String> authors,
        String contents,
        String datetime,
        String isbn,
        int price,
        String publisher,
        int sale_price,
        String status,
        String thumbnail,
        String title,
        List<String> translators,
        String url
) {
}
