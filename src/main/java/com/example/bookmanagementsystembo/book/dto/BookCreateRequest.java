package com.example.bookmanagementsystembo.book.dto;

import java.time.LocalDate;

public record BookCreateRequest(
        String isbn13,
        String isbn10,
        String title,
        String author,
        String publisher,
        LocalDate pubDate,
        String description,
        String coverUrl,
        Integer categoryId,
        String categoryName,
        int priceStandard,
        int priceSales,
        String stockStatus,
        int customerReviewRank,
        Integer seriesId,
        String seriesName,
        String mallType,
        String adultYn
) {
}
