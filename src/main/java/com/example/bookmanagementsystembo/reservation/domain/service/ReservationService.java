package com.example.bookmanagementsystembo.reservation.domain.service;

import com.example.bookmanagementsystembo.bookHold.entity.BookHold;
import com.example.bookmanagementsystembo.bookHold.enums.BookHoldStatus;
import com.example.bookmanagementsystembo.bookHold.repository.BookHoldRepository;
import com.example.bookmanagementsystembo.exception.CoreException;
import com.example.bookmanagementsystembo.exception.ErrorType;
import com.example.bookmanagementsystembo.reservation.domain.entity.Reservation;
import com.example.bookmanagementsystembo.reservation.enums.ReservationStatus;
import com.example.bookmanagementsystembo.reservation.infra.ReservationRepository;
import com.example.bookmanagementsystembo.reservation.presentation.dto.ReservationRes;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final BookHoldRepository bookHoldRepository;

    @Transactional
    public ReservationRes createReservation(Long bookId, Long userId) {
        List<BookHold> bookHolds = bookHoldRepository.findByBookId(bookId);

        // 1. AVAILABLE 상태의 BookHold가 있으면 예약 불가 (바로 대출 가능)
        boolean hasAvailable = bookHolds.stream()
                .anyMatch(bh -> bh.getStatus() == BookHoldStatus.AVAILABLE);
        if (hasAvailable) {
            throw new CoreException(ErrorType.BOOK_AVAILABLE_NO_RESERVATION, bookId);
        }

        // 2. 같은 bookId에 해당 userId의 WAITING 예약이 이미 있으면 중복 예약 불가
        List<Long> bookHoldIds = bookHolds.stream().map(BookHold::getBookHoldId).toList();
        if (!bookHoldIds.isEmpty() &&
                reservationRepository.existsByBookHold_BookHoldIdInAndUserIdAndStatus(bookHoldIds, userId, ReservationStatus.WAITING)) {
            throw new CoreException(ErrorType.RESERVATION_ALREADY_EXISTS, bookId);
        }

        // 3. 해당 userId의 WAITING 예약 총 건수가 2 이상이면 불가
        int userWaitingCount = reservationRepository.countByUserIdAndStatus(userId, ReservationStatus.WAITING);
        if (userWaitingCount >= 2) {
            throw new CoreException(ErrorType.USER_RESERVATION_LIMIT_EXCEEDED, userId);
        }

        // 4. 각 BookHold를 순회하여 WAITING 예약이 2명 미만인 hold 탐색
        BookHold targetHold = null;
        for (BookHold bh : bookHolds) {
            int waitingCount = reservationRepository.countByBookHold_BookHoldIdAndStatus(bh.getBookHoldId(), ReservationStatus.WAITING);
            if (waitingCount < 2) {
                targetHold = bh;
                break;
            }
        }
        if (targetHold == null) {
            throw new CoreException(ErrorType.RESERVATION_LIMIT_EXCEEDED, bookId);
        }

        // 5. 예약 생성
        Reservation reservation = Reservation.create(targetHold, userId, null);
        reservationRepository.save(reservation);

        return new ReservationRes(reservation.getReservationId(), reservation.getStatus());
    }

    @Transactional
    public void cancelReservation(Long reservationId, Long userId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new CoreException(ErrorType.RESERVATION_NOT_FOUND, reservationId));

        // IDOR 검증
        if (!reservation.getUserId().equals(userId)) {
            throw new CoreException(ErrorType.RESERVATION_NOT_OWNER, reservationId);
        }

        // 상태 검증
        if (reservation.getStatus() == ReservationStatus.EXPIRED) {
            throw new CoreException(ErrorType.RESERVATION_ALREADY_CANCELLED, reservationId);
        }

        reservation.expire();
    }
}
