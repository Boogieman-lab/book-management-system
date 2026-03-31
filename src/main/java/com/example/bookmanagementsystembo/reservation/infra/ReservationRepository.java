package com.example.bookmanagementsystembo.reservation.infra;

import com.example.bookmanagementsystembo.reservation.domain.entity.Reservation;
import com.example.bookmanagementsystembo.reservation.enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long>, ReservationQueryRepository {

    /** bookHoldId 기준 특정 상태의 예약 건수를 반환합니다. */
    int countByBookHoldIdAndStatus(Long bookHoldId, ReservationStatus status);

    /** bookHoldId 기준 특정 상태의 예약 목록을 생성일시 오름차순으로 조회합니다. */
    List<Reservation> findByBookHoldIdAndStatusOrderByCreatedAtAsc(Long bookHoldId, ReservationStatus status);

    /** bookHoldId 기준 특정 상태의 예약 중 가장 먼저 신청한 1건을 반환합니다. */
    Optional<Reservation> findFirstByBookHoldIdAndStatusOrderByCreatedAtAsc(Long bookHoldId, ReservationStatus status);

    /** 주어진 bookHoldId 목록과 userId, 상태로 예약 존재 여부를 확인합니다. */
    boolean existsByBookHoldIdInAndUserIdAndStatus(List<Long> bookHoldIds, Long userId, ReservationStatus status);

    /** bookHoldId, userId, 상태로 예약 단건을 조회합니다 (예약자 본인 검증용). */
    Optional<Reservation> findByBookHoldIdAndUserIdAndStatus(Long bookHoldId, Long userId, ReservationStatus status);

    /** 전체 주어진 상태의 예약 건수를 반환합니다. */
    long countByStatus(ReservationStatus status);

    /** 특정 사용자의 주어진 상태 예약 건수를 반환합니다. */
    int countByUserIdAndStatus(Long userId, ReservationStatus status);

    /** 특정 사용자의 주어진 상태 예약 목록을 조회합니다. */
    List<Reservation> findByUserIdAndStatus(Long userId, ReservationStatus status);

    /**
     * 4일 자동 승계 배치용: 특정 상태이면서 expireAt이 기준 일시 이전인 예약 목록을 조회합니다.
     */
    List<Reservation> findAllByStatusAndExpireAtBefore(ReservationStatus status, LocalDateTime before);
}
