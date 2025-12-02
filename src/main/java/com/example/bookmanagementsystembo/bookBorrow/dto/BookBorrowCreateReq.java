package com.example.bookmanagementsystembo.bookBorrow.dto;

public record BookBorrowCreateReq(Long bookHoldId, Long userId, String reason) {
}
