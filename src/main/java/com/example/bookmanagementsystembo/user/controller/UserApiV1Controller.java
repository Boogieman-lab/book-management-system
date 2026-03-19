package com.example.bookmanagementsystembo.user.controller;

import com.example.bookmanagementsystembo.bookBorrow.enums.BorrowStatus;
import com.example.bookmanagementsystembo.bookrequest.entity.BookRequest;
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
public class UserApiV1Controller {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<UserProfileRes> getMyProfile() {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(userService.getMyProfile(userId));
    }

    @PutMapping
    public ResponseEntity<UserProfileRes> updateMyProfile(@RequestBody UserUpdateReq request) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(userService.updateMyProfile(userId, request));
    }

    @GetMapping("/borrows")
    public ResponseEntity<UserBorrowPageRes> getMyBorrows(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) BorrowStatus status
    ) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(userService.getMyBorrows(userId, page, size, status));
    }

    @GetMapping("/requests")
    public ResponseEntity<UserRequestPageRes> getMyRequests(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Long userId = SecurityUtils.getCurrentUserId();
        Page<BookRequest> result = userService.getMyRequests(userId, page, size);

        List<UserRequestRes> items = result.getContent().stream()
                .map(UserRequestRes::from)
                .toList();

        UserRequestPageRes response = new UserRequestPageRes(
                items,
                result.getTotalElements(),
                result.getTotalPages(),
                page,
                size
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/reservations")
    public ResponseEntity<List<UserReservationRes>> getMyReservations() {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(userService.getMyReservations(userId));
    }
}
