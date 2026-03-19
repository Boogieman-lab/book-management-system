package com.example.bookmanagementsystembo.book.controller;

import com.example.bookmanagementsystembo.book.dto.BookCreateRequest;
import com.example.bookmanagementsystembo.book.dto.BookHoldCreateRequest;
import com.example.bookmanagementsystembo.book.dto.BookResponse;
import com.example.bookmanagementsystembo.book.dto.BookUpdateRequest;
import com.example.bookmanagementsystembo.book.service.BookService;
import com.example.bookmanagementsystembo.bookHold.dto.BookHoldResponse;
import com.example.bookmanagementsystembo.bookHold.dto.BookHoldStatusUpdateRequest;
import com.example.bookmanagementsystembo.bookHold.service.BookHoldService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 관리자 도서/재고 관리 API.
 *
 * POST   /api/v1/admin/books                              — 신규 도서 등록
 * PUT    /api/v1/admin/books/{bookId}                     — 도서 메타 수정
 * POST   /api/v1/admin/books/{bookId}/holds               — 재고 실물 +1 추가
 * PATCH  /api/v1/admin/book-holds/{bookHoldId}/status     — 보유본 상태 전환 (LOST 등)
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
public class AdminBookController {

    private final BookService bookService;
    private final BookHoldService bookHoldService;

    /** [관리자] 신규 도서 메타 등록 + BookHold 1건 자동 생성 */
    @PostMapping("/books")
    public ResponseEntity<BookResponse> registerBook(@RequestBody @Valid BookCreateRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookService.create(req));
    }

    /** [관리자] 기존 도서 메타 수정 (저자, 표지, 내용 등) */
    @PutMapping("/books/{bookId}")
    public ResponseEntity<BookResponse> updateBook(
            @PathVariable Long bookId,
            @RequestBody @Valid BookUpdateRequest req) {
        return ResponseEntity.ok(bookService.update(bookId, req));
    }

    /**
     * [관리자] 도서 실물 재고 +1.
     * 동일 bookId에 새 BookHold(AVAILABLE)를 추가합니다.
     */
    @PostMapping("/books/{bookId}/holds")
    public ResponseEntity<BookHoldResponse> addHold(
            @PathVariable Long bookId,
            @RequestBody BookHoldCreateRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookHoldService.addHold(bookId, req));
    }

    /**
     * [관리자] 도서 보유본 상태 전환.
     * LOST(분실) 처리 시 대출 중(BORROWED) 상태이면 400 예외를 반환합니다.
     */
    @PatchMapping("/book-holds/{bookHoldId}/status")
    public ResponseEntity<BookHoldResponse> updateHoldStatus(
            @PathVariable Long bookHoldId,
            @RequestBody @Valid BookHoldStatusUpdateRequest req) {
        return ResponseEntity.ok(bookHoldService.updateHoldStatus(bookHoldId, req));
    }
}
