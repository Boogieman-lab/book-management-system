package com.example.bookmanagementsystembo.user.domain.dto;

public record UserInfo(String userName, String email, String departmentName) {
    public static UserInfo of(String userName, String email, String departmentName) {
        return new UserInfo(userName, email, departmentName);
    }
}
