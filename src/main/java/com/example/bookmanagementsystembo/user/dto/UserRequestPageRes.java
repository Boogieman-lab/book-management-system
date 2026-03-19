package com.example.bookmanagementsystembo.user.dto;

import java.util.List;

public record UserRequestPageRes(
        List<UserRequestRes> items,
        long totalElements,
        int totalPages,
        int currentPage,
        int size
) {
}
