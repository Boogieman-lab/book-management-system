package com.example.bookmanagementsystembo.admin.dto;

/**
 * 관리자 대시보드 통계 응답 DTO.
 *
 * @param totalBorrows   현재 대출 건수 (BORROWED)
 * @param overdueCount   연체 건수 (OVERDUE)
 * @param reservations   예약 대기 건수 (WAITING)
 * @param pendingRequests 승인 대기 희망도서 신청 건수 (PENDING)
 */
public record AdminStatsOverviewResponse(
        Long totalBorrows,
        Long overdueCount,
        Long reservations,
        Long pendingRequests
) {
}
