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
import java.util.Optional;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class BookBorrowService {

    private final BookBorrowRepository bookBorrowRepository;
    private final BookHoldRepository bookHoldRepository;
    private final ReservationRepository reservationRepository;

    /** 전체 대출 목록을 요약 DTO로 반환합니다. */
    public List<BookBorrowDto> readAll() {
        return bookBorrowRepository.findBookBorrows();
    }

    /** 대출 ID로 상세 정보를 조회합니다. */
    public BookBorrowDetailDto read(Long bookBorrowId) {
        return bookBorrowRepository.findBookBorrow(bookBorrowId);
    }

    /**
     * 대출 상태를 변경합니다 (관리자용 레거시 API).
     * status 문자열은 {@link BorrowStatus} enum 이름과 일치해야 합니다.
     */
    @Transactional
    public void updateBookBorrow(Long bookBorrowId, String status) {
        BorrowStatus borrowStatus = BorrowStatus.fromString(status);
        BookBorrow bookBorrow = bookBorrowRepository.findById(bookBorrowId).orElseThrow(() -> new CoreException(ErrorType.BOOKBORROW_NOT_FOUND, bookBorrowId));
        bookBorrow.updateStatus(borrowStatus);
    }

    /**
     * 대출 레코드를 생성합니다 (레거시 API — hold 상태 변경 없음).
     * @return 생성된 bookBorrowId
     */
    @Transactional
    public Long createBookBorrow(Long bookHoldId, Long userId, String reason) {
        BookHold bookHold = bookHoldRepository.findById(bookHoldId)
                .orElseThrow(() -> new CoreException(ErrorType.BOOK_HOLD_NOT_FOUND, bookHoldId));
        BookBorrow bookBorrow = BookBorrow.create(bookHoldId, bookHold.getBookId(), userId, reason);
        return bookBorrowRepository.save(bookBorrow).getBookBorrowId();
    }

    /**
     * 도서를 대출합니다 (메인 API).
     * <ul>
     *   <li>비관적 락으로 BookHold를 선점합니다.</li>
     *   <li>AVAILABLE 상태가 아닌 경우 예외를 발생시킵니다.</li>
     *   <li>사용자의 현재 대출 권수가 10권 이상이면 예외를 발생시킵니다.</li>
     *   <li>대출 성공 시 BookHold 상태를 BORROWED로 변경합니다.</li>
     * </ul>
     */
    @Transactional
    public BorrowResponse borrow(Long bookHoldId, Long userId, String reason) {
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
        BookBorrow bookBorrow = BookBorrow.create(bookHoldId, bookHold.getBookId(), userId, reason);
        bookBorrowRepository.save(bookBorrow);

        // BookHold 상태 변경
        bookHold.updateStatus(BookHoldStatus.BORROWED);

        return new BorrowResponse(bookBorrow.getBookBorrowId(), bookBorrow.getDueDate());
    }

    /**
     * 도서를 반납합니다.
     * <ul>
     *   <li>IDOR 검증 — 본인의 대출만 반납 가능합니다.</li>
     *   <li>이미 반납된 대출은 예외를 발생시킵니다.</li>
     *   <li>반납 후 해당 hold에 WAITING 예약이 있으면 RESERVE_HOLD, 없으면 AVAILABLE로 전환합니다.</li>
     * </ul>
     */
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

    /** 사용자의 특정 도서에 대한 활성 대출(BORROWED/OVERDUE) ID를 반환합니다. 없으면 empty. */
    public Optional<Long> findMyActiveBorrowId(Long bookId, Long userId) {
        return bookBorrowRepository
                .findMyActiveBorrow(userId, bookId, List.of("BORROWED", "OVERDUE"))
                .map(BookBorrow::getBookBorrowId);
    }

    /** 관리자용 전체 대출 목록을 상태 필터 및 페이지네이션으로 조회합니다. status가 null이면 전체 조회. */
    public Page<AdminBorrowSummaryResponse> findAllForAdmin(BorrowStatus status, Pageable pageable) {
        return bookBorrowRepository.findAllForAdmin(status, pageable);
    }
}
