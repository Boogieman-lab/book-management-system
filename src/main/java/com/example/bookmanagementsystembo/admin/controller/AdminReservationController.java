package com.example.bookmanagementsystembo.admin.controller;

import com.example.bookmanagementsystembo.admin.dto.AdminReservationStatsResponse;
import com.example.bookmanagementsystembo.reservation.domain.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 관리자 예약 대기 API.
 *
 * GET /api/v1/admin/reservations — 전체 예약 대기 통계 조회
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/reservations")
public class AdminReservationController {

    private final ReservationService reservationService;

    /** 전체 예약 대기 목록을 대기 순위와 함께 조회합니다. */
    @GetMapping
    public ResponseEntity<AdminReservationStatsResponse> getWaitingStats() {
        return ResponseEntity.ok(reservationService.getWaitingStats());
    }
}
