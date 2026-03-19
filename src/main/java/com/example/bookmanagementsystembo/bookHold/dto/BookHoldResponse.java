package com.example.bookmanagementsystembo.bookHold.dto;

import com.example.bookmanagementsystembo.bookHold.entity.BookHold;
import com.example.bookmanagementsystembo.bookHold.enums.BookHoldStatus;

import java.time.LocalDateTime;

public record BookHoldResponse(Long bookHoldId, Long bookId, BookHoldStatus bookHoldStatus, String location, LocalDateTime createdAt, LocalDateTime updatedAt) {
    public static BookHoldResponse from(BookHold entity) {
        return new BookHoldResponse(
                entity.getBookHoldId(),
                entity.getBookId(),
                entity.getStatus(),
                entity.getLocation(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
