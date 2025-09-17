package com.example.bookmanagementsystembo.book.presentation.dto;

public record BookBorrowCreateRequest(Long bookHoldId, Long userId, String reason) {
}
