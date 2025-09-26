package com.example.bookmanagementsystembo.book.presentation.controller;

import com.example.bookmanagementsystembo.book.domain.dto.BookDto;
import com.example.bookmanagementsystembo.book.presentation.dto.BookCreateRequest;
import com.example.bookmanagementsystembo.book.presentation.dto.BookResponse;
import com.example.bookmanagementsystembo.book.domain.service.BookService;
import com.example.bookmanagementsystembo.book.presentation.dto.BookUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    // 도서 조회 (단수)
    @GetMapping("/{bookId}")
    public ResponseEntity<BookResponse> getBook(@PathVariable Long bookId) {
        BookDto book = bookService.getBookById(bookId);
        return ResponseEntity.ok(BookResponse.from(book));
    }


    // 도서 등록
    @PostMapping
    public ResponseEntity<Long> createBook(@RequestBody BookCreateRequest request) {
        Long bookId = bookService.createBook(request.toCommand());
        return ResponseEntity.ok(bookId);
    }

    // 도서 수정
    @PutMapping("/{bookId}")
    public ResponseEntity<Void> updateBook(@PathVariable Long bookId,
                                           @RequestBody BookUpdateRequest request) {
        bookService.updateBook(request.toCommand(bookId));
        return ResponseEntity.ok().build();
    }

    // 도서 삭제
    @DeleteMapping("/{bookId}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long bookId) {
        bookService.deleteBook(bookId);
        return ResponseEntity.noContent().build();
    }
}
