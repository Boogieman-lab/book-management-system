package com.example.bookmanagementsystembo.reservation.infra;

import com.example.bookmanagementsystembo.reservation.domain.entity.Reservation;
import com.example.bookmanagementsystembo.reservation.enums.ReservationStatus;

import java.util.List;
import java.util.Map;

public interface ReservationQueryRepository {

    /**
     * bookId 기준으로 해당 도서의 예약 목록을 예약일시 오름차순으로 조회합니다.
     * book_hold 테이블과 JOIN이 필요하여 QueryDSL로 구현합니다.
     */
    List<Reservation> findByBookIdAndStatus(Long bookId, ReservationStatus status);

    /**
     * bookHoldId 목록에 대해 bookHoldId → 도서 제목 맵을 단일 쿼리로 반환합니다.
     * 스케줄러 배치에서 N+1 없이 도서 제목을 일괄 조회할 때 사용합니다.
     */
    Map<Long, String> findBookTitlesByBookHoldIds(List<Long> bookHoldIds);
}
