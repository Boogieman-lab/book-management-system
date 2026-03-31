package com.example.bookmanagementsystembo.admin.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 관리자 예약 대기 통계 응답.
 * GET /api/v1/admin/reservations 응답으로 사용됩니다.
 */
public record AdminReservationStatsResponse(
        long totalWaiting,
        List<Item> items
) {
    public record Item(
            Long reservationId,
            Long bookId,
            String bookTitle,
            Long userId,
            String userName,
            long waitingOrder,
            LocalDateTime reservedAt
    ) {}
}
