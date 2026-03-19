package com.example.bookmanagementsystembo.bookBorrow.dto;

import java.time.LocalDateTime;
import java.util.List;

public record BookBorrowSummaryResponse(Long bookBorrowId, String title, String userName, String status, LocalDateTime createdAt) {
    public static BookBorrowSummaryResponse from(BookBorrowDto dto) {
        return new BookBorrowSummaryResponse(dto.bookBorrowId(), dto.title(), dto.userName(), dto.status(), dto.createdAt());
    }
    public static List<BookBorrowSummaryResponse> from(List<BookBorrowDto> dtos) {
        return dtos.stream()
                .map(BookBorrowSummaryResponse::from)
                .toList();
    }
}