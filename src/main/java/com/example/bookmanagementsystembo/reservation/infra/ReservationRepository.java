package com.example.bookmanagementsystembo.reservation.infra;

import com.example.bookmanagementsystembo.reservation.domain.entity.Reservation;
import com.example.bookmanagementsystembo.reservation.enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    /** 특정 BookHold에 대해 주어진 상태의 예약 건수를 반환합니다. */
    int countByBookHold_BookHoldIdAndStatus(Long bookHoldId, ReservationStatus status);

    /** BookHoldId 기준으로 주어진 상태의 예약 목록을 예약일시 오름차순으로 조회합니다. */
    List<Reservation> findByBookHold_BookHoldIdAndStatusOrderByCreatedAtAsc(Long bookHoldId, ReservationStatus status);

    /** 특정 사용자의 주어진 상태 예약 건수를 반환합니다. */
    int countByUserIdAndStatus(Long userId, ReservationStatus status);

    /** 특정 bookHold 목록과 사용자, 상태로 예약 존재 여부를 확인합니다. */
    boolean existsByBookHold_BookHoldIdInAndUserIdAndStatus(List<Long> bookHoldIds, Long userId, ReservationStatus status);

    /** 특정 사용자의 주어진 상태 예약 목록을 조회합니다. */
    List<Reservation> findByUserIdAndStatus(Long userId, ReservationStatus status);

    /** 특정 도서(bookId)의 주어진 상태 예약 목록을 예약일시 오름차순으로 조회합니다. */
    List<Reservation> findByBookHold_BookIdAndStatusOrderByReservedAtAsc(Long bookId, ReservationStatus status);
}
