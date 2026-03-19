package com.example.bookmanagementsystembo.user.dto;

import java.util.List;

public record UserBorrowPageRes(
        List<UserBorrowRes> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
}
