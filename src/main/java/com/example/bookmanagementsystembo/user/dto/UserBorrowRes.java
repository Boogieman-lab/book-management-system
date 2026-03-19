package com.example.bookmanagementsystembo.user.dto;

import java.time.LocalDateTime;

public record UserBorrowRes(
        String bookTitle,
        Long bookHoldId,
        LocalDateTime borrowDate,
        LocalDateTime dueDate,
        LocalDateTime returnDate,
        String status
) {
}
