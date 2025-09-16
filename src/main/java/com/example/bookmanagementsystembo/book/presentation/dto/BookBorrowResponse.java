package com.example.bookmanagementsystembo.book.presentation.dto;

import com.example.bookmanagementsystembo.book.dto.BookBorrowDto;

import java.time.LocalDateTime;
import java.util.List;

public record BookBorrowResponse(Long bookBorrowId, String title, String userName, String status, LocalDateTime createdAt) {
    public static BookBorrowResponse from(BookBorrowDto dto) {
        return new BookBorrowResponse(dto.bookBorrowId(), dto.title(), dto.userName(), dto.status(), dto.createdAt());
    }
    public static List<BookBorrowResponse> from(List<BookBorrowDto> dtos) {
        return dtos.stream()
                .map(BookBorrowResponse::from)
                .toList();
    }
}