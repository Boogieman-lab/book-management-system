package com.example.bookmanagementsystembo.bookRequest.controller;

import com.example.bookmanagementsystembo.bookRequest.service.BookRequestService;
import com.example.bookmanagementsystembo.bookRequest.dto.BookRequestCreateRequest;
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

}
