package com.example.bookmanagementsystembo.book.presentation.controller;

import com.example.bookmanagementsystembo.book.dto.BookDto;
import com.example.bookmanagementsystembo.book.presentation.dto.ExternalBookResponse;
import com.example.bookmanagementsystembo.book.service.ExternalBookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/external/books")
@RequiredArgsConstructor
@RestController
public class ExternalBookController {

    private final ExternalBookService externalBookService;

    /**
     * 제목으로 책 검색
     */
    @GetMapping("/getBooksByTitle")
    public ResponseEntity<List<ExternalBookResponse>> getBooksByTitle(@RequestParam String title) {
        List<BookDto> books = externalBookService.getBooksByTitle(title);
        return ResponseEntity.ok(ExternalBookResponse.from(books));
    }

}
