package com.example.bookmanagementsystembo.book.dto;

public record BookUpdateRequest(
        String title,
        String author,
        String publisher,
        String description,
        String coverUrl,
        int priceStandard,
        int priceSales,
        String stockStatus
) {
}
