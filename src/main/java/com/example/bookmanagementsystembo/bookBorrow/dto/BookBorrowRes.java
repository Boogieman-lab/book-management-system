package com.example.bookmanagementsystembo.bookBorrow.dto;

import java.time.LocalDateTime;
import java.util.List;

public record BookBorrowRes(Long bookBorrowId, String title, String userName, String status, LocalDateTime createdAt) {
    public static BookBorrowRes from(BookBorrowDto dto) {
        return new BookBorrowRes(dto.bookBorrowId(), dto.title(), dto.userName(), dto.status(), dto.createdAt());
    }
    public static List<BookBorrowRes> from(List<BookBorrowDto> dtos) {
        return dtos.stream()
                .map(BookBorrowRes::from)
                .toList();
    }
}