package com.example.bookmanagementsystembo.bookHold.service;

import com.example.bookmanagementsystembo.bookHold.dto.BookHoldRes;
import com.example.bookmanagementsystembo.bookHold.entity.BookHold;
import com.example.bookmanagementsystembo.bookHold.repository.BookHoldRepository;
import com.example.bookmanagementsystembo.exception.CoreException;
import com.example.bookmanagementsystembo.exception.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookHoldService {

    private final BookHoldRepository bookHoldRepository;

    public List<BookHoldRes> readAll(Long bookId) {
        return bookHoldRepository.findAll(bookId).stream().map(BookHoldRes::from).toList();
    }

    public BookHoldRes read(Long bookId, Long holdId) {
        BookHold hold = bookHoldRepository.findByBookHoldIdAndBookId(holdId, bookId)
                .orElseThrow(() -> new CoreException(ErrorType.BOOK_HOLD_NOT_FOUND, holdId));
        return BookHoldRes.from(hold);
    }
}
