package com.example.bookmanagementsystembo.bookHold.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 도서 보유본(BookHold)의 재고 상태 Enum.
 * AVAILABLE    : 대출 가능
 * BORROWED     : 현재 대출 중
 * RESERVE_HOLD : 예약자에 의해 홀드된 상태 (반납 후 대기 중)
 * LOST         : 분실
 */
@RequiredArgsConstructor
@Getter
public enum BookHoldStatus {
    AVAILABLE("대출 가능"),
    BORROWED("대출 중"),
    RESERVE_HOLD("예약 대기"),
    LOST("분실");
    private final String description;
}
