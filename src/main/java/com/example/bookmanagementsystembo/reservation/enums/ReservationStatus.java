package com.example.bookmanagementsystembo.reservation.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 예약(Reservation) 상태 Enum.
 * WAITING : 예약 대기 중 (반납 후 픽업 가능 알림 전)
 * EXPIRED : 예약 만료 (픽업 기한 초과 또는 자동 취소)
 */
@Getter
@RequiredArgsConstructor
public enum ReservationStatus {
    WAITING("예약 대기"),
    EXPIRED("예약 만료");

    private final String desc;
}
