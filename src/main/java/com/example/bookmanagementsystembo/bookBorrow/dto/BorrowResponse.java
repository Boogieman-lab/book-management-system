package com.example.bookmanagementsystembo.bookBorrow.dto;

import java.time.LocalDateTime;

public record BorrowResponse(Long bookBorrowId, LocalDateTime dueDate) {
}
