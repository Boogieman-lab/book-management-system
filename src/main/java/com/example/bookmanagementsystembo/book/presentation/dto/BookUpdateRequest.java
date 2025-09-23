package com.example.bookmanagementsystembo.book.presentation.dto;

import com.example.bookmanagementsystembo.book.domain.dto.BookCreateDto;
import com.example.bookmanagementsystembo.book.domain.dto.BookUpdateDto;

import java.time.LocalDateTime;
import java.util.List;

public record BookUpdateRequest(
        String title,
        List<String> authors,
        String publisher
) {
    public BookUpdateDto toCommand(Long bookId) {
        return new BookUpdateDto(bookId, title, authors, publisher);
    }
}
