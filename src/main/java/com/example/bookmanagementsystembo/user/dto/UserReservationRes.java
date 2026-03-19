package com.example.bookmanagementsystembo.user.dto;

import com.example.bookmanagementsystembo.reservation.enums.ReservationStatus;

import java.time.LocalDateTime;

public record UserReservationRes(
        Long reservationId,
        String bookTitle,
        ReservationStatus status,
        LocalDateTime reservedAt,
        LocalDateTime expireAt
) {
}
