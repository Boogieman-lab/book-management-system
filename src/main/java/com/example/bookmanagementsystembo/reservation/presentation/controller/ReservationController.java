package com.example.bookmanagementsystembo.reservation.presentation.controller;

import com.example.bookmanagementsystembo.common.SecurityUtils;
import com.example.bookmanagementsystembo.reservation.domain.service.ReservationService;
import com.example.bookmanagementsystembo.reservation.presentation.dto.ReservationCreateRequest;
import com.example.bookmanagementsystembo.reservation.presentation.dto.ReservationResponse;
import com.example.bookmanagementsystembo.reservation.presentation.dto.ReservationWaitingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    /** 특정 도서의 대기 예약 목록 조회 */
    @GetMapping
    public ResponseEntity<List<ReservationWaitingResponse>> getWaitingReservations(@RequestParam Long bookId) {
        return ResponseEntity.ok(reservationService.getWaitingReservations(bookId));
    }

    /** 예약 등록 */
    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(@RequestBody ReservationCreateRequest req) {
        Long userId = SecurityUtils.getCurrentUserId();
        ReservationResponse res = reservationService.createReservation(req.bookId(), userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    /** 예약 취소 */
    @DeleteMapping("/{reservationId}")
    public ResponseEntity<Void> cancelReservation(@PathVariable Long reservationId) {
        Long userId = SecurityUtils.getCurrentUserId();
        reservationService.cancelReservation(reservationId, userId);
        return ResponseEntity.noContent().build();
    }
}
