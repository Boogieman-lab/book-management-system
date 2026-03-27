package com.example.bookmanagementsystembo.user.dto;

import java.util.List;

/**
 * 관리자 임직원 목록 페이지 응답 DTO.
 */
public record AdminUserPageResponse(
        List<AdminUserListResponse> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
}
