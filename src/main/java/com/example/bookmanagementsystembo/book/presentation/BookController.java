package com.example.bookmanagementsystembo.book.presentation;

import com.example.bookmanagementsystembo.book.entity.Book;
import com.example.bookmanagementsystembo.book.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/external/books")
@RequiredArgsConstructor
@RestController
public class BookController {

    private final BookService bookService;

    /**
     * 제목으로 책 검색
     */
    @GetMapping("/getBooksByTitle")
    public ResponseEntity<List<Book>> getBooksByTitle(@RequestParam String title) {
        List<Book> books = bookService.getBooksByTitle(title);
        return ResponseEntity.ok(books);
    }

}
