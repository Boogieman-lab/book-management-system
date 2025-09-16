package com.example.bookmanagementsystembo.book.dto;

import java.time.LocalDateTime;

public record BookBorrowDto(Long bookBorrowId, String title, String userName, String status, LocalDateTime createdAt) {
}
