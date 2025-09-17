package com.example.bookmanagementsystembo.book.service;

import com.example.bookmanagementsystembo.book.dto.BookBorrowDetailDto;
import com.example.bookmanagementsystembo.book.dto.BookBorrowDto;
import com.example.bookmanagementsystembo.book.entity.BookBorrow;
import com.example.bookmanagementsystembo.book.enums.BorrowStatus;
import com.example.bookmanagementsystembo.book.infra.BookBorrowRepository;
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

    public List<BookBorrowDto> getBookBorrows() {
        return bookBorrowRepository.findBookBorrows();
    }

    public BookBorrowDetailDto getBookBorrow(Long bookBorrowId) {
        return bookBorrowRepository.findBookBorrow(bookBorrowId);
    }

    @Transactional
    public void updateBookBorrow(Long bookBorrowId, String status) {
        BorrowStatus borrowStatus = BorrowStatus.fromString(status);
        BookBorrow bookBorrow = bookBorrowRepository.findById(bookBorrowId).orElseThrow(() -> new CoreException(ErrorType.BOOKBORROW_NOT_FOUND, bookBorrowId));
        bookBorrow.updateStatus(borrowStatus);
    }

    public Long createBookBorrow(Long bookHoldId, Long userId, String reason) {
        BookBorrow bookBorrow = BookBorrow.create(bookHoldId, userId, reason);
        return bookBorrowRepository.save(bookBorrow).getBookBorrowId();
    }
}
