package com.example.bookmanagementsystembo.bookHold.service;

import com.example.bookmanagementsystembo.book.dto.BookHoldCreateRequest;
import com.example.bookmanagementsystembo.bookHold.dto.BookHoldResponse;
import com.example.bookmanagementsystembo.bookHold.dto.BookHoldStatusUpdateRequest;
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

    public List<BookHoldResponse> readAll(Long bookId) {
        return bookHoldRepository.findByBookId(bookId).stream().map(BookHoldResponse::from).toList();
    }

    public BookHoldResponse read(Long bookId, Long holdId) {
        BookHold hold = bookHoldRepository.findByBookHoldIdAndBookId(holdId, bookId)
                .orElseThrow(() -> new CoreException(ErrorType.BOOK_HOLD_NOT_FOUND, holdId));
        return BookHoldResponse.from(hold);
    }

    @Transactional
    public BookHoldResponse addHold(Long bookId, BookHoldCreateRequest req) {
        BookHold hold = BookHold.createWithLocation(bookId, req.location());
        return BookHoldResponse.from(bookHoldRepository.save(hold));
    }

    @Transactional
    public BookHoldResponse updateHoldStatus(Long bookHoldId, BookHoldStatusUpdateRequest req) {
        BookHold hold = bookHoldRepository.findById(bookHoldId)
                .orElseThrow(() -> new CoreException(ErrorType.BOOK_HOLD_NOT_FOUND, bookHoldId));

        // [예외] 대출 중(BORROWED) 상태인 도서는 LOST/DISCARDED로 직접 전환 불가
        if (hold.getStatus() == BookHoldStatus.BORROWED
                && (req.status() == BookHoldStatus.LOST || req.status() == BookHoldStatus.DISCARDED)) {
            throw new CoreException(ErrorType.BOOK_HOLD_CANNOT_CHANGE_BORROWED, bookHoldId);
        }

        hold.updateStatus(req.status());
        return BookHoldResponse.from(hold);
    }
}
