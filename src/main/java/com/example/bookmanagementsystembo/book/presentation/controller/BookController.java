package com.example.bookmanagementsystembo.book.presentation.controller;

import com.example.bookmanagementsystembo.book.domain.service.BookService;
import com.example.bookmanagementsystembo.book.enums.BookSearchField;
import com.example.bookmanagementsystembo.book.presentation.dto.BookCreateRequest;
import com.example.bookmanagementsystembo.book.presentation.dto.BookDetailResponse;
import com.example.bookmanagementsystembo.book.presentation.dto.BookResponse;
import com.example.bookmanagementsystembo.book.presentation.dto.BookUpdateRequest;
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
//    @GetMapping("/{bookId}")
//    public ResponseEntity<BookResponse> getBookDetail(@PathVariable Long bookId) {
//        BookDto book = bookService.getBookById(bookId);
//        return ResponseEntity.ok(BookResponse.from(book));
//    }


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

    // 조회조건별 도서 검색 (복수)
    @GetMapping
    public List<BookResponse> searchBooks(
            @RequestParam String field,
            @RequestParam String query) {
        BookSearchField enumField = BookSearchField.valueOf(field.toUpperCase());
        return bookService.searchBooks(enumField, query);
    }

    // 도서 상세
    @GetMapping("/detail/{bookId}")
    @ResponseBody
    public BookDetailResponse getBookDetail(@PathVariable Long bookId) {
        BookDetailResponse bookDetail = bookService.getBookDetail(bookId);
        return bookDetail;
    }

}
