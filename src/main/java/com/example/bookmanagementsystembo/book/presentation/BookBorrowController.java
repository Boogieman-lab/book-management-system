package com.example.bookmanagementsystembo.book.presentation;

import com.example.bookmanagementsystembo.book.dto.BookBorrowDetailDto;
import com.example.bookmanagementsystembo.book.dto.BookBorrowDto;
import com.example.bookmanagementsystembo.book.presentation.dto.BookBorrowDetailResponse;
import com.example.bookmanagementsystembo.book.presentation.dto.BookBorrowResponse;
import com.example.bookmanagementsystembo.book.service.BookBorrowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("/{borrowId}")
    public ResponseEntity<BookBorrowDetailResponse> getBookBorrows(@PathVariable Long borrowId) {
        BookBorrowDetailDto bookBorrows = bookBorrowService.getBookBorrow(borrowId);
        return ResponseEntity.ok(BookBorrowDetailResponse.from(bookBorrows));
    }

}
