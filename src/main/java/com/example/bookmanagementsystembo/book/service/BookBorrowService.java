package com.example.bookmanagementsystembo.book.service;

import com.example.bookmanagementsystembo.book.dto.BookBorrowDetailDto;
import com.example.bookmanagementsystembo.book.dto.BookBorrowDto;
import com.example.bookmanagementsystembo.book.infra.BookBorrowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class BookBorrowService {

    private final BookBorrowRepository bookBorrowRepository;

    public List<BookBorrowDto> getBookBorrows() {
        return bookBorrowRepository.findBookBorrows();
    }

    public BookBorrowDetailDto getBookBorrow(Long bookBorrowId) {
        return bookBorrowRepository.findBookBorrow(bookBorrowId);
    }
}
