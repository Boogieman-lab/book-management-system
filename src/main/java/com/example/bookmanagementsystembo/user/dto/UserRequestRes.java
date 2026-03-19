package com.example.bookmanagementsystembo.user.dto;

import com.example.bookmanagementsystembo.bookrequest.dto.BookRequestStatus;
import com.example.bookmanagementsystembo.bookrequest.entity.BookRequest;

import java.time.LocalDateTime;

public record UserRequestRes(
        Long bookRequestId,
        String title,
        String authors,
        String isbn,
        BookRequestStatus status,
        LocalDateTime createdAt
) {
    public static UserRequestRes from(BookRequest entity) {
        return new UserRequestRes(
                entity.getBookRequestId(),
                entity.getTitle(),
                entity.getAuthors(),
                entity.getIsbn(),
                entity.getStatus(),
                entity.getCreatedAt()
        );
    }
}
