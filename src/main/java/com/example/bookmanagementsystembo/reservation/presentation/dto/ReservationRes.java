package com.example.bookmanagementsystembo.reservation.presentation.dto;

import com.example.bookmanagementsystembo.reservation.enums.ReservationStatus;

public record ReservationRes(Long reservationId, ReservationStatus status) {
}
