package com.example.bookmanagementsystembo.bookBorrow.service;

import com.example.bookmanagementsystembo.bookBorrow.dto.BookBorrowDetailDto;
import com.example.bookmanagementsystembo.bookBorrow.dto.BookBorrowDto;
import com.example.bookmanagementsystembo.bookBorrow.entity.BookBorrow;
import com.example.bookmanagementsystembo.bookBorrow.enums.BorrowStatus;
import com.example.bookmanagementsystembo.bookBorrow.repository.BookBorrowRepository;
import com.example.bookmanagementsystembo.exception.CoreException;
import com.example.bookmanagementsystembo.exception.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class BookBorrowService {

    private final BookBorrowRepository bookBorrowRepository;

    public List<BookBorrowDto> readAll() {
        return bookBorrowRepository.findBookBorrows();
    }

    public BookBorrowDetailDto read(Long bookBorrowId) {
        return bookBorrowRepository.findBookBorrow(bookBorrowId);
    }

    @Transactional
    public void updateBookBorrow(Long bookBorrowId, String status) {
        BorrowStatus borrowStatus = BorrowStatus.fromString(status);
        BookBorrow bookBorrow = bookBorrowRepository.findById(bookBorrowId).orElseThrow(() -> new CoreException(ErrorType.BOOKBORROW_NOT_FOUND, bookBorrowId));
        bookBorrow.updateStatus(borrowStatus);
    }

    @Transactional
    public Long createBookBorrow(Long bookHoldId, Long userId, String reason) {
        BookBorrow bookBorrow = BookBorrow.create(bookHoldId, userId, reason);
        return bookBorrowRepository.save(bookBorrow).getBookBorrowId();
    }
}
