package com.example.bookmanagementsystembo.book.presentation.dto;

import com.example.bookmanagementsystembo.book.dto.ExternalBookDto;

import java.util.List;

public record ExternalBookResponse(
        List<String> authors,
        String contents,
        String datetime,
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

    // 단일 DTO → Response 변환
    public static ExternalBookResponse from(ExternalBookDto dto) {
        return new ExternalBookResponse(
                dto.authors(),
                dto.contents(),
                dto.datetime(),
                dto.isbn(),
                dto.price(),
                dto.publisher(),
                dto.sale_price(),
                dto.status(),
                dto.thumbnail(),
                dto.title(),
                dto.translators(),
                dto.url()
        );
    }

    // 리스트 변환
    public static List<ExternalBookResponse> from(List<ExternalBookDto> dtos) {
        return dtos.stream()
                .map(ExternalBookResponse::from)
                .toList();
    }
}
