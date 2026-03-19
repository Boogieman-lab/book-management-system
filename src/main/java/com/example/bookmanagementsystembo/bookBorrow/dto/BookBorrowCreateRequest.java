package com.example.bookmanagementsystembo.bookBorrow.dto;

public record BookBorrowCreateRequest(Long bookHoldId, Long userId, String reason) {
}
