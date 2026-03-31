package com.example.bookmanagementsystembo.reservation.domain.service;

import com.example.bookmanagementsystembo.bookHold.entity.BookHold;
import com.example.bookmanagementsystembo.bookHold.enums.BookHoldStatus;
import com.example.bookmanagementsystembo.bookHold.repository.BookHoldRepository;
import com.example.bookmanagementsystembo.exception.CoreException;
import com.example.bookmanagementsystembo.exception.ErrorType;
import com.example.bookmanagementsystembo.reservation.domain.entity.Reservation;
import com.example.bookmanagementsystembo.reservation.enums.ReservationStatus;
import com.example.bookmanagementsystembo.reservation.infra.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 예약 배치 처리 서비스.
 * 스케줄러에서 호출하며, 상태 변경 트랜잭션과 알림 발송을 분리하여
 * 알림 시스템 장애가 DB 상태 변경 롤백을 유발하지 않도록 격리합니다.
 */
@Service
@RequiredArgsConstructor
public class ReservationBatchService {

    private final ReservationRepository reservationRepository;
    private final BookHoldRepository bookHoldRepository;

    /**
     * 픽업 기한(expireAt)이 지난 NOTIFIED 예약을 EXPIRED로 일괄 변경합니다.
     *
     * @param now 현재 일시 (expireAt 비교 기준)
     * @return EXPIRED로 변경된 예약 목록
     */
    @Transactional
    public List<Reservation> markExpiredReservations(LocalDateTime now) {
        List<Reservation> expired = reservationRepository
                .findAllByStatusAndExpireAtBefore(ReservationStatus.NOTIFIED, now);
        expired.forEach(Reservation::expire);
        return expired;
    }

    /**
     * 다음 대기자로 예약을 승계합니다.
     * <ul>
     *   <li>다음 WAITING 예약자가 있으면 NOTIFIED로 전환하고 해당 userId를 반환합니다.</li>
     *   <li>대기자가 없으면 BookHold를 AVAILABLE로 전환하고 빈 Optional을 반환합니다.</li>
     * </ul>
     * 이 메서드 완료 후 호출 측에서 알림 발송을 별도로 수행하세요.
     *
     * @param bookHoldId 승계 대상 BookHold ID
     * @return 승계된 예약자의 userId (대기자 없으면 empty)
     */
    @Transactional
    public Optional<Long> promoteNextWaiting(Long bookHoldId) {
        Optional<Reservation> next = reservationRepository
                .findFirstByBookHoldIdAndStatusOrderByCreatedAtAsc(bookHoldId, ReservationStatus.WAITING);

        if (next.isPresent()) {
            next.get().notifyPickup(LocalDateTime.now().plusDays(4));
            return Optional.of(next.get().getUserId());
        }

        BookHold bookHold = bookHoldRepository.findById(bookHoldId)
                .orElseThrow(() -> new CoreException(ErrorType.BOOK_HOLD_NOT_FOUND, bookHoldId));
        bookHold.updateStatus(BookHoldStatus.AVAILABLE);
        return Optional.empty();
    }
}
