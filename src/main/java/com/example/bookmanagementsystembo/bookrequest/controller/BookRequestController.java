package com.example.bookmanagementsystembo.bookRequest.controller;

import com.example.bookmanagementsystembo.bookRequest.service.BookRequestService;
import com.example.bookmanagementsystembo.bookRequest.dto.BookRequestCreateRequest;
import com.example.bookmanagementsystembo.bookRequest.dto.BookRequestPageResponse;
import com.example.bookmanagementsystembo.bookRequest.dto.BookRequestResponse;
import com.example.bookmanagementsystembo.bookRequest.enums.BookRequestStatus;
import com.example.bookmanagementsystembo.bookRequest.dto.BookRequestSummaryPageResponse;
import com.example.bookmanagementsystembo.bookRequest.dto.BookRequestSummaryResponse;
import com.example.bookmanagementsystembo.bookRequest.dto.BookRequestUpdateRequest;
import com.example.bookmanagementsystembo.user.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api")
@RestController
@RequiredArgsConstructor
public class BookRequestController {

    private final BookRequestService bookRequestService;

    @GetMapping("/book-requests")
    public ResponseEntity<BookRequestSummaryPageResponse> readAll(
            @RequestParam("page") Long page,
            @RequestParam("pageSize") Long pageSize
    ) {
        return ResponseEntity.ok(bookRequestService.readAll(page, pageSize));
    }

    @GetMapping("/book-requests/{bookRequestId}")
    public ResponseEntity<BookRequestSummaryResponse> read(@PathVariable("bookRequestId") Long bookRequestId) {
        return ResponseEntity.ok(bookRequestService.read(bookRequestId));
    }

    @PostMapping("/book-requests")
    public ResponseEntity<BookRequestSummaryResponse> create(@RequestBody BookRequestCreateRequest request, @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(bookRequestService.create(userDetails.getUserId(), request));
    }

    @PutMapping("/book-requests/{bookRequestId}")
    public ResponseEntity<BookRequestSummaryResponse> update(
            @PathVariable Long bookRequestId,
            @RequestBody BookRequestUpdateRequest request
    ) {
        return ResponseEntity.ok(bookRequestService.update(bookRequestId, request));
    }

    // ── V1 API ──────────────────────────────────────────────────────────────

    @PostMapping("/v1/book-requests")
    public ResponseEntity<BookRequestResponse> createV1(
            @RequestBody BookRequestCreateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ResponseEntity.ok(bookRequestService.createV1(userDetails.getUserId(), request));
    }

    @GetMapping("/v1/book-requests")
    public ResponseEntity<BookRequestPageResponse> readMyRequestsV1(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) BookRequestStatus status,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        return ResponseEntity.ok(bookRequestService.readAllV1(page, size, status, userDetails.getUserId(), isAdmin));
    }

    @DeleteMapping("/v1/book-requests/{bookRequestId}")
    public ResponseEntity<Void> cancelV1(
            @PathVariable Long bookRequestId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        bookRequestService.cancelV1(bookRequestId, userDetails.getUserId());
        return ResponseEntity.noContent().build();
    }

}
