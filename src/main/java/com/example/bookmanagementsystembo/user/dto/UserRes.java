package com.example.bookmanagementsystembo.user.dto;

public record UserRes(String userName, String email, String departmentName) {

    public static UserRes of(String userName, String email, String departmentName) {
        return new UserRes(userName, email, departmentName);
    }
}
