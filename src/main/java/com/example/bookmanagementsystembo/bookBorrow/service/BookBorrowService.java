package com.example.bookmanagementsystembo.bookBorrow.service;

import com.example.bookmanagementsystembo.bookBorrow.dto.*;
import com.example.bookmanagementsystembo.bookBorrow.entity.BookBorrow;
import com.example.bookmanagementsystembo.bookBorrow.enums.BorrowStatus;
import com.example.bookmanagementsystembo.bookBorrow.repository.BookBorrowRepository;
import com.example.bookmanagementsystembo.bookHold.entity.BookHold;
import com.example.bookmanagementsystembo.bookHold.enums.BookHoldStatus;
import com.example.bookmanagementsystembo.bookHold.repository.BookHoldRepository;
import com.example.bookmanagementsystembo.exception.CoreException;
import com.example.bookmanagementsystembo.exception.ErrorType;
import com.example.bookmanagementsystembo.reservation.domain.entity.Reservation;
import com.example.bookmanagementsystembo.reservation.enums.ReservationStatus;
import com.example.bookmanagementsystembo.reservation.infra.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class BookBorrowService {

    private final BookBorrowRepository bookBorrowRepository;
    private final BookHoldRepository bookHoldRepository;
    private final ReservationRepository reservationRepository;

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

    @Transactional
    public BorrowRes borrow(Long bookHoldId, Long userId, String reason) {
        // 비관적 락으로 BookHold 조회
        BookHold bookHold = bookHoldRepository.findByIdForUpdate(bookHoldId)
                .orElseThrow(() -> new CoreException(ErrorType.BOOK_HOLD_NOT_FOUND, bookHoldId));

        // AVAILABLE 상태 확인
        if (bookHold.getStatus() != BookHoldStatus.AVAILABLE) {
            throw new CoreException(ErrorType.BOOK_NOT_AVAILABLE, bookHoldId);
        }

        // 대출 한도 확인 (최대 10권)
        int currentBorrowCount = bookBorrowRepository.countByUserIdAndStatus(userId, BorrowStatus.BORROWED);
        if (currentBorrowCount >= 10) {
            throw new CoreException(ErrorType.BORROW_LIMIT_EXCEEDED, userId);
        }

        // 대출 처리
        BookBorrow bookBorrow = BookBorrow.create(bookHoldId, userId, reason);
        bookBorrowRepository.save(bookBorrow);

        // BookHold 상태 변경
        bookHold.updateStatus(BookHoldStatus.BORROWED);

        return new BorrowRes(bookBorrow.getBookBorrowId(), bookBorrow.getDueDate());
    }

    @Transactional
    public void returnBook(Long borrowId, Long userId) {
        BookBorrow bookBorrow = bookBorrowRepository.findById(borrowId)
                .orElseThrow(() -> new CoreException(ErrorType.BOOKBORROW_NOT_FOUND, borrowId));

        // IDOR 검증
        if (!bookBorrow.getUserId().equals(userId)) {
            throw new CoreException(ErrorType.BORROW_NOT_OWNER, borrowId);
        }

        // 이미 반납된 경우
        if (bookBorrow.getStatus() == BorrowStatus.RETURNED) {
            throw new CoreException(ErrorType.BORROW_ALREADY_RETURNED, borrowId);
        }

        // 반납 처리
        bookBorrow.returnBook();

        // BookHold 상태 결정
        Long bookHoldId = bookBorrow.getBookHoldId();
        int waitingCount = reservationRepository.countByBookHold_BookHoldIdAndStatus(bookHoldId, ReservationStatus.WAITING);

        BookHold bookHold = bookHoldRepository.findById(bookHoldId)
                .orElseThrow(() -> new CoreException(ErrorType.BOOK_HOLD_NOT_FOUND, bookHoldId));

        if (waitingCount > 0) {
            bookHold.updateStatus(BookHoldStatus.RESERVE_HOLD);
            List<Reservation> waitingReservations = reservationRepository
                    .findByBookHold_BookHoldIdAndStatusOrderByCreatedAtAsc(bookHoldId, ReservationStatus.WAITING);
            Reservation firstReservation = waitingReservations.get(0);
            log.info("예약 알림: userId={} 에게 bookHoldId={} 도서 픽업 알림 발송 (알림 도메인 미구현, 로그 대체)",
                    firstReservation.getUserId(), bookHoldId);
        } else {
            bookHold.updateStatus(BookHoldStatus.AVAILABLE);
        }
    }

    public Page<AdminBorrowRes> findAllForAdmin(BorrowStatus status, Pageable pageable) {
        return bookBorrowRepository.findAllForAdmin(status, pageable);
    }
}
