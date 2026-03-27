package com.example.bookmanagementsystembo.user.dto;

import java.time.LocalDateTime;

public record UserBorrowResponse(
        Long borrowId,
        Long bookId,
        String bookTitle,
        String author,
        String publisher,
        String coverUrl,
        Long bookHoldId,
        LocalDateTime borrowDate,
        LocalDateTime dueDate,
        LocalDateTime returnDate,
        String status
) {
}
