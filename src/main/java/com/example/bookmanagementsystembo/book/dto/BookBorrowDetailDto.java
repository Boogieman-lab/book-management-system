package com.example.bookmanagementsystembo.book.dto;

import java.time.LocalDateTime;

public record BookBorrowDetailDto(
        Long bookBorrowId,
        String title,
        String userName,
        String status,
        LocalDateTime createdAt,
        String reason,
        String departmentName
) {
}
