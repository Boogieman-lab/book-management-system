package com.example.bookmanagementsystembo.bookRequest.dto;

import com.example.bookmanagementsystembo.bookRequest.entity.BookRequest;

import java.time.LocalDateTime;

public record BookRequestResponse(
        Long bookRequestId,
        Long userId,
        String title,
        String authors,
        String publisher,
        String isbn,
        String reason,
        BookRequestStatus status,
        String rejectReason,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static BookRequestResponse from(BookRequest entity) {
        return new BookRequestResponse(
                entity.getBookRequestId(),
                entity.getUserId(),
                entity.getTitle(),
                entity.getAuthors(),
                entity.getPublisher(),
                entity.getIsbn(),
                entity.getReason(),
                entity.getStatus(),
                entity.getRejectReason(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
