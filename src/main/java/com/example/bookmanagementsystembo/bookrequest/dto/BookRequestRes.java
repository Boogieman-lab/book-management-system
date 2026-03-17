package com.example.bookmanagementsystembo.bookrequest.dto;


import com.example.bookmanagementsystembo.bookrequest.entity.BookRequest;

import java.time.LocalDateTime;

public record BookRequestRes(
        Long bookRequestId,
        Long userId,
        String title,
        String authors,
        String publisher,
        String isbn,
        String reason,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static BookRequestRes from(BookRequest entity) {
        return new BookRequestRes(
                entity.getBookRequestId(),
                entity.getUserId(),
                entity.getTitle(),
                entity.getAuthors(),
                entity.getPublisher(),
                entity.getIsbn(),
                entity.getReason(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}