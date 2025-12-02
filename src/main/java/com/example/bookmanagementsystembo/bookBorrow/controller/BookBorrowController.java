package com.example.bookmanagementsystembo.bookBorrow.controller;

import com.example.bookmanagementsystembo.bookBorrow.dto.BookBorrowDetailDto;
import com.example.bookmanagementsystembo.bookBorrow.dto.BookBorrowDto;
import com.example.bookmanagementsystembo.bookBorrow.dto.BookBorrowCreateReq;
import com.example.bookmanagementsystembo.bookBorrow.dto.BookBorrowDetailRes;
import com.example.bookmanagementsystembo.bookBorrow.dto.BookBorrowRes;
import com.example.bookmanagementsystembo.bookBorrow.service.BookBorrowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api")
@RequiredArgsConstructor
@RestController
public class BookBorrowController {

    private final BookBorrowService bookBorrowService;

    @GetMapping
    public ResponseEntity<List<BookBorrowRes>> readAll() {
        List<BookBorrowDto> bookBorrows = bookBorrowService.readAll();
        return ResponseEntity.ok(BookBorrowRes.from(bookBorrows));
    }

    @GetMapping("/books/{bookId}/borrow{bookBorrowId}")
    public ResponseEntity<BookBorrowDetailRes> getBookBorrows(@PathVariable Long bookBorrowId) {
        BookBorrowDetailDto bookBorrows = bookBorrowService.read(bookBorrowId);
        return ResponseEntity.ok(BookBorrowDetailRes.from(bookBorrows));
    }

    @PatchMapping("/{bookBorrowId}")
    public ResponseEntity<Void> updateBookBorrowStatus(@PathVariable Long bookBorrowId, @RequestParam String status) {
        bookBorrowService.updateBookBorrow(bookBorrowId, status);
        return ResponseEntity.ok().build();
    }

    @PostMapping
    public ResponseEntity<Long> createBookBorrow(@RequestBody BookBorrowCreateReq request) {
        return ResponseEntity.ok(bookBorrowService.createBookBorrow(request.bookHoldId(), request.userId(), request.reason()));
    }
}
