package com.example.bookmanagementsystembo.user.dto;

import com.example.bookmanagementsystembo.reservation.enums.ReservationStatus;

import java.time.LocalDateTime;

public record UserReservationResponse(
        Long reservationId,
        String bookTitle,
        ReservationStatus status,
        LocalDateTime reservedAt,
        LocalDateTime expireAt,
        Long waitingOrder
) {
}
