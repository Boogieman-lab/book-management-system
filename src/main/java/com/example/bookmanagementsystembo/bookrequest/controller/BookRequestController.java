package com.example.bookmanagementsystembo.bookrequest.controller;

import com.example.bookmanagementsystembo.bookrequest.service.BookRequestService;
import com.example.bookmanagementsystembo.bookrequest.dto.BookRequestCreateReq;
import com.example.bookmanagementsystembo.bookrequest.dto.BookRequestPageRes;
import com.example.bookmanagementsystembo.bookrequest.dto.BookRequestRes;
import com.example.bookmanagementsystembo.bookrequest.dto.BookRequestUpdateReq;
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
    public ResponseEntity<BookRequestPageRes> readAll(
            @RequestParam("page") Long page,
            @RequestParam("pageSize") Long pageSize
    ) {
        return ResponseEntity.ok(bookRequestService.readAll(page, pageSize));
    }

    @GetMapping("/book-requests/{bookRequestId}")
    public ResponseEntity<BookRequestRes> read(@PathVariable("bookRequestId") Long bookRequestId) {
        return ResponseEntity.ok(bookRequestService.read(bookRequestId));
    }

    @PostMapping("/book-requests")
    public ResponseEntity<BookRequestRes> create(@RequestBody BookRequestCreateReq request, @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(bookRequestService.create(userDetails.getUserId(), request));
    }

    @PutMapping("/book-requests/{bookRequestId}")
    public ResponseEntity<BookRequestRes> update(
            @PathVariable Long bookRequestId,
            @RequestBody BookRequestUpdateReq request
    ) {
        return ResponseEntity.ok(bookRequestService.update(bookRequestId, request));
    }

}
