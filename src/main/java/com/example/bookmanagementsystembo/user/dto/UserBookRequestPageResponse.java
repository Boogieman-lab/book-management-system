package com.example.bookmanagementsystembo.user.dto;

import java.util.List;

public record UserBookRequestPageResponse(
        List<UserBookRequestResponse> items,
        long totalElements,
        int totalPages,
        int currentPage,
        int size
) {
}
