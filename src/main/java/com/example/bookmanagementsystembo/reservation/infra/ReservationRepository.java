package com.example.bookmanagementsystembo.reservation.infra;

import com.example.bookmanagementsystembo.reservation.domain.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    int countByBookHold_BookHoldIdAndStatus(Long bookHoldId, String status);

    // BookHoldId 기준, 상태가 ACTIVE인 예약만 조회
    List<Reservation> findByBookHold_BookHoldIdAndStatusOrderByCreatedAtAsc(Long bookHoldId, String status);


}
