package com.example.bookmanagementsystembo.bookBorrow.dto;

import java.time.LocalDateTime;

public record BookBorrowDetailRes(
        Long bookBorrowId, String title, String userName, String status, LocalDateTime createdAt,
        String reason, String departmentName
) {
    public static BookBorrowDetailRes from(BookBorrowDetailDto dto) {
        return new BookBorrowDetailRes(
                dto.bookBorrowId(), dto.title(), dto.userName(), dto.status(), dto.createdAt(),
                dto.reason(), dto.departmentName()
        );
    }
}