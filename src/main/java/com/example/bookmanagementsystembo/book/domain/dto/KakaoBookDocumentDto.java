package com.example.bookmanagementsystembo.book.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.List;

public record KakaoBookDocumentDto(
        String title,
        List<String> authors,
        List<String> translators,
        String contents,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
        LocalDateTime datetime,
        String isbn,
        int price,
        String publisher,
        @JsonProperty("sale_price") int salePrice,
        String status,
        String thumbnail,
        String url
) {
}
