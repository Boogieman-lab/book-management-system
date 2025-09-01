package com.example.bookmanagementsystembo.user.domain.dto.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Role {
    ROLE_USER("사용자"),
    ROLE_ADMIN("관리자");

    private final String roleName;
}
