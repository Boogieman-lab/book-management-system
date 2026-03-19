package com.example.bookmanagementsystembo.reservation.presentation.dto;

import com.example.bookmanagementsystembo.reservation.enums.ReservationStatus;

public record ReservationResponse(Long reservationId, ReservationStatus status) {
}
