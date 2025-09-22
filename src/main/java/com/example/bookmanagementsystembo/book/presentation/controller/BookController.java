package com.example.bookmanagementsystembo.book.presentation.controller;

import com.example.bookmanagementsystembo.book.domain.dto.BookDto;
import com.example.bookmanagementsystembo.book.presentation.dto.BookCreateRequest;
import com.example.bookmanagementsystembo.book.presentation.dto.BookResponse;
import com.example.bookmanagementsystembo.book.domain.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    // 도서 단일 조회 (bookId 기준)
    @GetMapping("/{bookId}")
    public ResponseEntity<BookResponse> getBook(@PathVariable Long bookId) {
        BookDto book = bookService.getBookById(bookId);
        return ResponseEntity.ok(BookResponse.from(book));
    }


    // 보유 도서 등록
    @PostMapping
    public ResponseEntity<Long> createBook(@RequestBody BookCreateRequest request) {
        Long bookId = bookService.createBook(request.toCommand());
        return ResponseEntity.ok(bookId);
    }
}
