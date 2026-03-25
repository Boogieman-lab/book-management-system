package com.example.bookmanagementsystembo.user.dto;

import com.example.bookmanagementsystembo.bookRequest.enums.BookRequestStatus;
import com.example.bookmanagementsystembo.bookRequest.entity.BookRequest;

import java.time.LocalDateTime;

public record UserBookRequestResponse(
        Long bookRequestId,
        String title,
        String authors,
        String isbn,
        BookRequestStatus status,
        LocalDateTime createdAt
) {
    public static UserBookRequestResponse from(BookRequest entity) {
        return new UserBookRequestResponse(
                entity.getBookRequestId(),
                entity.getTitle(),
                entity.getAuthors(),
                entity.getIsbn(),
                entity.getStatus(),
                entity.getCreatedAt()
        );
    }
}
