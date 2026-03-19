package com.example.bookmanagementsystembo.book.controller;

import com.example.bookmanagementsystembo.book.enums.BookSearchField;
import com.example.bookmanagementsystembo.book.dto.BookCreateRequest;
import com.example.bookmanagementsystembo.book.dto.BookResponse;
import com.example.bookmanagementsystembo.book.service.BookService;
import com.example.bookmanagementsystembo.book.dto.BookUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    // 도서 상세 조회 (단수)
    @GetMapping("/{bookId}")
    public ResponseEntity<BookResponse> read(@PathVariable Long bookId) {
        return ResponseEntity.ok(bookService.read(bookId));
    }

    // 도서 등록
    @PostMapping
    public ResponseEntity<BookResponse> create(@RequestBody BookCreateRequest request) {
        return ResponseEntity.ok(bookService.create(request));
    }

    // 도서 수정₩
    @PutMapping("/{bookId}")
    public ResponseEntity<BookResponse> update(@PathVariable Long bookId, @RequestBody BookUpdateRequest request) {
        return ResponseEntity.ok(bookService.update(bookId, request));
    }

    // 도서 삭제
    @DeleteMapping("/{bookId}")
    public ResponseEntity<Void> delete(@PathVariable Long bookId) {
        bookService.delete(bookId);
        return ResponseEntity.noContent().build();
    }

    // 조회조건별 도서 검색 (복수)
    @GetMapping
    public List<BookResponse> searchBooks(@RequestParam BookSearchField field, @RequestParam String query) {
        return bookService.searchBooks(field, query);
    }

}
