package com.example.bookmanagementsystembo.bookHold.controller;

import com.example.bookmanagementsystembo.bookHold.dto.BookHoldResponse;
import com.example.bookmanagementsystembo.bookHold.service.BookHoldService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class BookHoldController {

    private final BookHoldService bookHoldService;

    @GetMapping("/books/{bookId}/holds")
    public ResponseEntity<List<BookHoldResponse>> readAll(@PathVariable Long bookId){
        return ResponseEntity.ok(bookHoldService.readAll(bookId));
    }


    @GetMapping("/books/{bookId}/holds/{holdId}")
    public ResponseEntity<BookHoldResponse> read(@PathVariable Long bookId, @PathVariable Long holdId){
        return ResponseEntity.ok(bookHoldService.read(bookId, holdId));
    }

//    @GetMapping("/books/{bookId}/holds/{holdId}")
//    public ResponseEntity<BookHoldResponse> update(@PathVariable Long bookId, @PathVariable Long holdId){
//        return ResponseEntity.ok(bookHoldService.update(bookId, holdId));
//    }

}
