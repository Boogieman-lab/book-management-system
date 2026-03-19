package com.example.bookmanagementsystembo.bookBorrow.dto;

import java.time.LocalDateTime;

public record AdminBorrowSummaryResponse(
        Long bookBorrowId,
        String bookTitle,
        String userName,
        String status,
        LocalDateTime borrowDate,
        LocalDateTime dueDate,
        LocalDateTime returnDate
) {
}
