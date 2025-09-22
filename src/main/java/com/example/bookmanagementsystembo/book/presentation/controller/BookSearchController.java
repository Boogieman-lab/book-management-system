package com.example.bookmanagementsystembo.book.presentation.controller;

import com.example.bookmanagementsystembo.book.domain.dto.KakaoBookSearchDto;
import com.example.bookmanagementsystembo.book.presentation.dto.BookSearchResponse;
import com.example.bookmanagementsystembo.book.domain.service.BookSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/search/books")
@RequiredArgsConstructor
@RestController
public class BookSearchController {

    private final BookSearchService bookSearchService;

    /**
     * 제목으로 책 검색
     */
    @GetMapping
    public ResponseEntity<BookSearchResponse> getBooksByTitle(@RequestParam String title) {
        KakaoBookSearchDto books = bookSearchService.getBooksByTitle(title);
        return ResponseEntity.ok(BookSearchResponse.from(books.documents(),books.meta()));
    }

}
