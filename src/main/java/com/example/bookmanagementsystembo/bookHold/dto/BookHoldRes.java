package com.example.bookmanagementsystembo.bookHold.dto;

import com.example.bookmanagementsystembo.bookHold.entity.BookHold;
import com.example.bookmanagementsystembo.bookHold.enums.BookHoldStatus;

import java.time.LocalDateTime;

public record BookHoldRes(Long bookHoldId, Long bookId, BookHoldStatus bookHoldStatus, String location, LocalDateTime createdAt, LocalDateTime updatedAt) {
    public static BookHoldRes from(BookHold entity) {
        return new BookHoldRes(
                entity.getBookHoldId(),
                entity.getBookId(),
                entity.getStatus(),
                entity.getLocation(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
