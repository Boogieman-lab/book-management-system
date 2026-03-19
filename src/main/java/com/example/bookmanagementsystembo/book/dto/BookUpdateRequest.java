package com.example.bookmanagementsystembo.book.dto;

import java.util.List;

public record BookUpdateRequest(
        String title,
        String contents,
        String url,
        List<String> authors,
        List<String> translators,
        String publisher,
        Integer price,
        Integer salePrice,
        String thumbnail
) {
}
