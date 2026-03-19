package com.example.bookmanagementsystembo.user.dto;

import com.example.bookmanagementsystembo.user.entity.Users;
import com.example.bookmanagementsystembo.user.enums.Role;

import java.time.LocalDateTime;

public record UserProfileRes(
        Long userId,
        String email,
        String name,
        String departmentName,
        String profileImage,
        Role role,
        LocalDateTime createdAt
) {
    public static UserProfileRes of(Users user, String departmentName) {
        return new UserProfileRes(
                user.getUserId(),
                user.getEmail(),
                user.getName(),
                departmentName,
                user.getProfileImage(),
                user.getRole(),
                user.getCreatedAt()
        );
    }
}
