package com.example.bookmanagementsystembo.bookBorrow.dto;

import java.util.List;

public record BorrowPageResponse(
        List<AdminBorrowSummaryResponse> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
}
