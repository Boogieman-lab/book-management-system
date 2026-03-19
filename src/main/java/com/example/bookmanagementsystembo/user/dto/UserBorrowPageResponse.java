package com.example.bookmanagementsystembo.user.dto;

import java.util.List;

public record UserBorrowPageResponse(
        List<UserBorrowResponse> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
}
