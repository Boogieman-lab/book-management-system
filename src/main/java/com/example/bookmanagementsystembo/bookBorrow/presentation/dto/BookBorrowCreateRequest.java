package com.example.bookmanagementsystembo.bookBorrow.presentation.dto;

public record BookBorrowCreateRequest(Long bookHoldId, Long userId, String reason) {
}
