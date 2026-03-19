package com.example.bookmanagementsystembo.user.dto;

public record UserResponse(String userName, String email, String departmentName) {

    public static UserResponse of(String userName, String email, String departmentName) {
        return new UserResponse(userName, email, departmentName);
    }
}
