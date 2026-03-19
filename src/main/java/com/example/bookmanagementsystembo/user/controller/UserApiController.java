package com.example.bookmanagementsystembo.user.controller;

import com.example.bookmanagementsystembo.bookBorrow.enums.BorrowStatus;
import com.example.bookmanagementsystembo.bookRequest.entity.BookRequest;
import com.example.bookmanagementsystembo.common.SecurityUtils;
import com.example.bookmanagementsystembo.user.dto.*;
import com.example.bookmanagementsystembo.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users/me")
public class UserApiController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<UserProfileResponse> getMyProfile() {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(userService.getMyProfile(userId));
    }

    @PutMapping
    public ResponseEntity<UserProfileResponse> updateMyProfile(@RequestBody UserUpdateRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(userService.updateMyProfile(userId, request));
    }

    @GetMapping("/borrows")
    public ResponseEntity<UserBorrowPageResponse> getMyBorrows(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) BorrowStatus status
    ) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(userService.getMyBorrows(userId, page, size, status));
    }

    @GetMapping("/requests")
    public ResponseEntity<UserBookRequestPageResponse> getMyRequests(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Long userId = SecurityUtils.getCurrentUserId();
        Page<BookRequest> result = userService.getMyRequests(userId, page, size);

        List<UserBookRequestResponse> items = result.getContent().stream()
                .map(UserBookRequestResponse::from)
                .toList();

        UserBookRequestPageResponse response = new UserBookRequestPageResponse(
                items,
                result.getTotalElements(),
                result.getTotalPages(),
                page,
                size
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/reservations")
    public ResponseEntity<List<UserReservationResponse>> getMyReservations() {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(userService.getMyReservations(userId));
    }
}
