package com.example.bookmanagementsystembo.bookRequest.dto;

import java.util.List;

public record BookRequestSummaryPageResponse(List<BookRequestSummaryResponse> items, Long count) {
    public static BookRequestSummaryPageResponse of(List<BookRequestSummaryResponse> bookRequests, Long bookRequestCount) {
        return new BookRequestSummaryPageResponse(bookRequests, bookRequestCount);
    }
}
