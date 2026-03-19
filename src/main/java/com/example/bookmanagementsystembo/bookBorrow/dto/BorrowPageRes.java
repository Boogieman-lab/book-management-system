package com.example.bookmanagementsystembo.bookBorrow.dto;

import java.util.List;

public record BorrowPageRes(
        List<AdminBorrowRes> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
}
