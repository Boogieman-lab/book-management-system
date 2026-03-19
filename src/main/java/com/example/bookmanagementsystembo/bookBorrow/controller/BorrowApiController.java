package com.example.bookmanagementsystembo.bookBorrow.controller;

import com.example.bookmanagementsystembo.bookBorrow.dto.BorrowCreateReq;
import com.example.bookmanagementsystembo.bookBorrow.dto.BorrowRes;
import com.example.bookmanagementsystembo.bookBorrow.service.BookBorrowService;
import com.example.bookmanagementsystembo.common.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 도서 대출/반납 V1 API.
 *
 * POST /api/v1/borrows               — 도서 대출
 * POST /api/v1/borrows/{borrowId}/return — 도서 반납
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/borrows")
public class BorrowApiController {

    private final BookBorrowService bookBorrowService;

    /** 도서 대출 */
    @PostMapping
    public ResponseEntity<BorrowRes> borrow(@RequestBody BorrowCreateReq req) {
        Long userId = SecurityUtils.getCurrentUserId();
        BorrowRes res = bookBorrowService.borrow(req.bookHoldId(), userId, req.reason());
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
