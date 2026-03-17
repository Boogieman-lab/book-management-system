package com.example.bookmanagementsystembo.reservation.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReservationStatus {
    REQUESTED("예약 요청"),
    CANCELLED("예약 취소"),
    COMPLETED("대출 완료");

    private final String desc;
}
