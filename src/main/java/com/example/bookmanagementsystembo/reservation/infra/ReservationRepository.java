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
}
