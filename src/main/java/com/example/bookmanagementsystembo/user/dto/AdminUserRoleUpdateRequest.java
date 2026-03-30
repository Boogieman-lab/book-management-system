package com.example.bookmanagementsystembo.user.dto;

import com.example.bookmanagementsystembo.user.enums.Role;
import jakarta.validation.constraints.NotNull;

/**
 * 관리자 임직원 권한 변경 요청 DTO.
 */
public record AdminUserRoleUpdateRequest(@NotNull Role role) {
}
