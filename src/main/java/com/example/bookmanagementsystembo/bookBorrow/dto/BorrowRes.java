package com.example.bookmanagementsystembo.bookBorrow.dto;

import java.time.LocalDateTime;

public record BorrowRes(Long bookBorrowId, LocalDateTime dueDate) {
}
