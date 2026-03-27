package com.example.bookmanagementsystembo.user.controller;

import com.example.bookmanagementsystembo.user.dto.AdminUserPageResponse;
import com.example.bookmanagementsystembo.user.dto.AdminUserRoleUpdateRequest;
import com.example.bookmanagementsystembo.user.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 관리자 임직원 관리 API.
 *
 * GET   /api/v1/admin/users             — 임직원 목록 조회 (페이지네이션)
 * PATCH /api/v1/admin/users/{id}/role   — 권한 변경 (ROLE_USER ↔ ROLE_ADMIN)
 */
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/users")
public class AdminUserController {

    private final UserService userService;

    /** 임직원 목록 조회 */
    @GetMapping
    public ResponseEntity<AdminUserPageResponse> getUsers(
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size
    ) {
        return ResponseEntity.ok(userService.findAllForAdmin(page, size));
    }

    /** 임직원 권한 변경 */
    @PatchMapping("/{userId}/role")
    public ResponseEntity<Void> updateRole(
            @PathVariable Long userId,
            @Valid @RequestBody AdminUserRoleUpdateRequest request
    ) {
        userService.updateRole(userId, request.role());
        return ResponseEntity.ok().build();
    }
}
