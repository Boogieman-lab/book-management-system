package com.example.bookmanagementsystembo.reservation.presentation.controller;

import com.example.bookmanagementsystembo.common.SecurityUtils;
import com.example.bookmanagementsystembo.reservation.domain.service.ReservationService;
import com.example.bookmanagementsystembo.reservation.presentation.dto.ReservationCreateReq;
import com.example.bookmanagementsystembo.reservation.presentation.dto.ReservationRes;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    /** 예약 등록 */
    @PostMapping
    public ResponseEntity<ReservationRes> createReservation(@RequestBody ReservationCreateReq req) {
        Long userId = SecurityUtils.getCurrentUserId();
        ReservationRes res = reservationService.createReservation(req.bookId(), userId);
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
