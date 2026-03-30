package com.example.bookmanagementsystembo.bookRequest.controller;

import com.example.bookmanagementsystembo.bookRequest.dto.BookRequestPageResponse;
import com.example.bookmanagementsystembo.bookRequest.dto.BookRequestResponse;
import com.example.bookmanagementsystembo.bookRequest.dto.BookRequestStatusUpdateRequest;
import com.example.bookmanagementsystembo.bookRequest.enums.BookRequestStatus;
import com.example.bookmanagementsystembo.bookRequest.service.BookRequestService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 관리자 희망도서 신청 결재 API.
 *
 * GET   /api/v1/admin/book-requests            — 신청 목록 조회 (전체, 상태 필터)
 * PATCH /api/v1/admin/book-requests/{id}/status — 승인 / 거절 처리
 */
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/book-requests")
public class AdminBookRequestController {

    private final BookRequestService bookRequestService;

    /** 희망도서 신청 목록 조회 (관리자 전체 조회) */
    @GetMapping
    public ResponseEntity<BookRequestPageResponse> getBookRequests(
            @RequestParam(required = false) BookRequestStatus status,
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size
    ) {
        return ResponseEntity.ok(bookRequestService.readAllV1(page, size, status, null, true));
    }

    /** 희망도서 신청 승인 / 거절 처리 */
    @PatchMapping("/{requestId}/status")
    public ResponseEntity<BookRequestResponse> updateStatus(
            @PathVariable Long requestId,
            @Valid @RequestBody BookRequestStatusUpdateRequest request
    ) {
        return ResponseEntity.ok(
                bookRequestService.updateStatus(requestId, request.status(), request.rejectReason())
        );
    }
}
