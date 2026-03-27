package com.example.bookmanagementsystembo.user.dto;

import com.example.bookmanagementsystembo.user.entity.Users;
import com.example.bookmanagementsystembo.user.enums.Role;

import java.time.LocalDateTime;

/**
 * 관리자용 임직원 목록 응답 DTO.
 */
public record AdminUserListResponse(
        Long userId,
        String name,
        String email,
        String departmentName,
        Role role,
        boolean isLocked,
        LocalDateTime createdAt
) {
    public static AdminUserListResponse from(Users user, String departmentName) {
        return new AdminUserListResponse(
                user.getUserId(),
                user.getName(),
                user.getEmail(),
                departmentName,
                user.getRole(),
                user.isLocked(),
                user.getCreatedAt()
        );
    }
}
