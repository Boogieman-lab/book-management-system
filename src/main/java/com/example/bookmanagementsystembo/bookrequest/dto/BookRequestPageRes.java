package com.example.bookmanagementsystembo.bookrequest.dto;

import java.util.List;

public record BookRequestPageRes(List<BookRequestRes> items, Long count) {
    public static BookRequestPageRes of(List<BookRequestRes> bookRequests, Long bookRequestCount) {
        return new BookRequestPageRes(bookRequests, bookRequestCount);
    }
}
