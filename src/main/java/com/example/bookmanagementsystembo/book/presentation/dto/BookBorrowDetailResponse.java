package com.example.bookmanagementsystembo.book.presentation.dto;

import com.example.bookmanagementsystembo.book.domain.dto.BookBorrowDetailDto;

import java.time.LocalDateTime;

public record BookBorrowDetailResponse(
        Long bookBorrowId, String title, String userName, String status, LocalDateTime createdAt,
        String reason, String departmentName
) {
    public static BookBorrowDetailResponse from(BookBorrowDetailDto dto) {
        return new BookBorrowDetailResponse(
                dto.bookBorrowId(), dto.title(), dto.userName(), dto.status(), dto.createdAt(),
                dto.reason(), dto.departmentName()
        );
    }
}