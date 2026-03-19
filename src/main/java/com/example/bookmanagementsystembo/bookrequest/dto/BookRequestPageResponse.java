package com.example.bookmanagementsystembo.bookRequest.dto;

import java.util.List;

public record BookRequestPageResponse(
        List<BookRequestResponse> items,
        long totalElements,
        int totalPages,
        int currentPage,
        int size
) {
    public static BookRequestPageResponse of(List<BookRequestResponse> items, long totalElements, int totalPages, int currentPage, int size) {
        return new BookRequestPageResponse(items, totalElements, totalPages, currentPage, size);
    }
}
