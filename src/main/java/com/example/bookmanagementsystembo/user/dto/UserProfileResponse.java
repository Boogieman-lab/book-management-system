package com.example.bookmanagementsystembo.user.dto;

import com.example.bookmanagementsystembo.user.entity.Users;
import com.example.bookmanagementsystembo.user.enums.Role;

import java.time.LocalDateTime;

public record UserProfileResponse(
        Long userId,
        String email,
        String name,
        String departmentName,
        String profileImage,
        Role role,
        LocalDateTime createdAt
) {
    public static UserProfileResponse of(Users user, String departmentName) {
        return new UserProfileResponse(
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
