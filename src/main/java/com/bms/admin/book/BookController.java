package com.bms.admin.book;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping("/fetch-books")
    public String fetchBooks() {
        bookService.fetchAndSaveBooks();
        return "Books fetched and saved successfully!";
    }
}
