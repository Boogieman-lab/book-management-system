package com.example.bookmanagementsystembo.bookHold.service;

import com.example.bookmanagementsystembo.book.dto.BookHoldAddReq;
import com.example.bookmanagementsystembo.bookHold.dto.BookHoldRes;
import com.example.bookmanagementsystembo.bookHold.dto.BookHoldStatusUpdateReq;
import com.example.bookmanagementsystembo.bookHold.entity.BookHold;
import com.example.bookmanagementsystembo.bookHold.enums.BookHoldStatus;
import com.example.bookmanagementsystembo.bookHold.repository.BookHoldRepository;
import com.example.bookmanagementsystembo.exception.CoreException;
import com.example.bookmanagementsystembo.exception.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookHoldService {

    private final BookHoldRepository bookHoldRepository;

    public List<BookHoldRes> readAll(Long bookId) {
        return bookHoldRepository.findByBookId(bookId).stream().map(BookHoldRes::from).toList();
    }

    public BookHoldRes read(Long bookId, Long holdId) {
        BookHold hold = bookHoldRepository.findByBookHoldIdAndBookId(holdId, bookId)
                .orElseThrow(() -> new CoreException(ErrorType.BOOK_HOLD_NOT_FOUND, holdId));
        return BookHoldRes.from(hold);
    }

    @Transactional
    public BookHoldRes addHold(Long bookId, BookHoldAddReq req) {
        BookHold hold = BookHold.createWithLocation(bookId, req.location());
        return BookHoldRes.from(bookHoldRepository.save(hold));
    }

    @Transactional
    public BookHoldRes updateHoldStatus(Long bookHoldId, BookHoldStatusUpdateReq req) {
        BookHold hold = bookHoldRepository.findById(bookHoldId)
                .orElseThrow(() -> new CoreException(ErrorType.BOOK_HOLD_NOT_FOUND, bookHoldId));

        // [예외] 대출 중(BORROWED) 상태인 도서는 LOST/DISCARDED로 직접 전환 불가
        if (hold.getStatus() == BookHoldStatus.BORROWED
                && (req.status() == BookHoldStatus.LOST || req.status() == BookHoldStatus.DISCARDED)) {
            throw new CoreException(ErrorType.BOOK_HOLD_CANNOT_CHANGE_BORROWED, bookHoldId);
        }

        hold.updateStatus(req.status());
        return BookHoldRes.from(hold);
    }
}
