package com.example.bookmanagementsystembo.book.dto;

import java.time.LocalDateTime;
import java.util.List;

public record BookCreateReq(
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
        String thumbnail
) {
}
