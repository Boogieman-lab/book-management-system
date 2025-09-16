package com.example.bookmanagementsystembo.book.presentation.controller;

import com.example.bookmanagementsystembo.book.dto.BookBorrowDetailDto;
import com.example.bookmanagementsystembo.book.dto.BookBorrowDto;
import com.example.bookmanagementsystembo.book.presentation.dto.BookBorrowDetailResponse;
import com.example.bookmanagementsystembo.book.presentation.dto.BookBorrowResponse;
import com.example.bookmanagementsystembo.book.service.BookBorrowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/borrows/")
@RequiredArgsConstructor
@RestController
public class BookBorrowController {

    private final BookBorrowService bookBorrowService;

    @GetMapping
    public ResponseEntity<List<BookBorrowResponse>> getBookBorrows() {
        List<BookBorrowDto> bookBorrows = bookBorrowService.getBookBorrows();
        return ResponseEntity.ok(BookBorrowResponse.from(bookBorrows));
    }

    @GetMapping("/{bookBorrowId}")
    public ResponseEntity<BookBorrowDetailResponse> getBookBorrows(@PathVariable Long bookBorrowId) {
        BookBorrowDetailDto bookBorrows = bookBorrowService.getBookBorrow(bookBorrowId);
        return ResponseEntity.ok(BookBorrowDetailResponse.from(bookBorrows));
    }

    @PatchMapping("/{bookBorrowId}")
    public ResponseEntity<Void> updateBookBorrowStatus(@PathVariable Long bookBorrowId, @RequestParam String status) {
        bookBorrowService.updateBookBorrow(bookBorrowId, status);
        return ResponseEntity.ok().build();
    }

}
