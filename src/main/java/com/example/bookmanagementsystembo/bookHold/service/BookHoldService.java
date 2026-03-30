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

    /** 도서 ID에 속한 모든 BookHold 목록을 반환합니다. */
    public List<BookHoldResponse> readAll(Long bookId) {
        return bookHoldRepository.findByBookId(bookId).stream().map(BookHoldResponse::from).toList();
    }

    /** bookId와 holdId 조합으로 단건 BookHold를 조회합니다. 존재하지 않으면 예외를 발생시킵니다. */
    public BookHoldResponse read(Long bookId, Long holdId) {
        BookHold hold = bookHoldRepository.findByBookHoldIdAndBookId(holdId, bookId)
                .orElseThrow(() -> new CoreException(ErrorType.BOOK_HOLD_NOT_FOUND, holdId));
        return BookHoldResponse.from(hold);
    }

    /** 특정 도서에 BookHold(실물)를 1개 추가합니다. 초기 상태는 AVAILABLE입니다. */
    @Transactional
    public BookHoldResponse addHold(Long bookId, BookHoldCreateRequest req) {
        BookHold hold = BookHold.createWithLocation(bookId, req.location());
        return BookHoldResponse.from(bookHoldRepository.save(hold));
    }

    /**
     * BookHold 상태를 변경합니다 (관리자용).
     * BORROWED 상태인 도서는 LOST/DISCARDED로 직접 전환할 수 없습니다.
     */
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
        if (req.location() != null) {
            hold.updateLocation(req.location());
        }
        return BookHoldResponse.from(hold);
    }
}
