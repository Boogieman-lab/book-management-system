package com.example.bookmanagementsystembo.reservation.presentation.dto;

import java.time.LocalDateTime;

public record ReservationWaitingResponse(Long userId, LocalDateTime reservedAt) {
}
