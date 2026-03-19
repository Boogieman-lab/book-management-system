package com.example.bookmanagementsystembo.user.dto;

import java.time.LocalDateTime;

public record UserBorrowResponse(
        String bookTitle,
        Long bookHoldId,
        LocalDateTime borrowDate,
        LocalDateTime dueDate,
        LocalDateTime returnDate,
        String status
) {
}
