package com.example.bookmanagementsystembo.book.dto;

import com.example.bookmanagementsystembo.book.entity.Book;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.List;

public record BookDto(
        List<String> authors,
        List<String> translators,
        String contents,
        String datetime,
        String isbn,
        int price,
        int salePrice,
        String publisher,
        String status,
        String thumbnail,
        String title,
        String url
) {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static BookDto from(Book book) {
        List<String> authorsList;
        List<String> translatorsList;

        try {
            if (book.getAuthors() != null && !book.getAuthors().isEmpty()) {
                authorsList = objectMapper.readValue(book.getAuthors(), new TypeReference<List<String>>() {});
            } else {
                authorsList = Collections.emptyList();
            }

            if (book.getTranslators() != null && !book.getTranslators().isEmpty()) {
                translatorsList = objectMapper.readValue(book.getTranslators(), new TypeReference<List<String>>() {});
            } else {
                translatorsList = Collections.emptyList();
            }
        } catch (Exception e) {
            authorsList = Collections.emptyList();
            translatorsList = Collections.emptyList();
        }

        return new BookDto(
                authorsList,
                translatorsList,
                book.getContents(),
                book.getDatetime() != null ? book.getDatetime().toString() : null,
                book.getIsbn(),
                book.getPrice(),
                book.getSalePrice(),
                book.getPublisher(),
                book.getStatus(),
                book.getThumbnail(),
                book.getTitle(),
                book.getUrl()
        );
    }
}
