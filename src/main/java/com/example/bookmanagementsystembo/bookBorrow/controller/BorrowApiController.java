package com.example.bookmanagementsystembo.bookBorrow.controller;

import com.example.bookmanagementsystembo.bookBorrow.dto.BorrowCreateRequest;
import com.example.bookmanagementsystembo.bookBorrow.dto.BorrowResponse;
import com.example.bookmanagementsystembo.bookBorrow.service.BookBorrowService;
import com.example.bookmanagementsystembo.common.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 도서 대출/반납 V1 API.
 *
 * GET  /api/v1/borrows/me?bookId=    — 현재 사용자의 특정 도서 활성 대출 조회
 * POST /api/v1/borrows               — 도서 대출
 * POST /api/v1/borrows/{borrowId}/return — 도서 반납
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/borrows")
public class BorrowApiController {

    private final BookBorrowService bookBorrowService;

    /** 현재 사용자의 특정 도서 활성 대출 조회 (BORROWED / OVERDUE) */
    @GetMapping("/me")
    public ResponseEntity<?> getMyBorrow(@RequestParam Long bookId) {
        Long userId = SecurityUtils.getCurrentUserId();
        return bookBorrowService.findMyActiveBorrowId(bookId, userId)
                .map(borrowId -> ResponseEntity.ok(Map.of("borrowId", borrowId)))
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    /** 도서 대출 */
    @PostMapping
    public ResponseEntity<BorrowResponse> borrow(@RequestBody BorrowCreateRequest req) {
        Long userId = SecurityUtils.getCurrentUserId();
        BorrowResponse res = bookBorrowService.borrow(req.bookHoldId(), userId, req.reason());
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    /** 도서 반납 */
    @PostMapping("/{borrowId}/return")
    public ResponseEntity<Void> returnBook(@PathVariable Long borrowId) {
        Long userId = SecurityUtils.getCurrentUserId();
        bookBorrowService.returnBook(borrowId, userId);
        return ResponseEntity.ok().build();
    }
}
