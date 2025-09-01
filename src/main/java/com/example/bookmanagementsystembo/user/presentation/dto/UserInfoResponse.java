package com.example.bookmanagementsystembo.user.presentation.dto;

import com.example.bookmanagementsystembo.user.domain.dto.UserInfo;

public record UserInfoResponse(String userName, String email, String departmentName) {

    public static UserInfoResponse from(UserInfo dto) {
        return new UserInfoResponse(dto.userName(), dto.email(), dto.departmentName());
    }
}
