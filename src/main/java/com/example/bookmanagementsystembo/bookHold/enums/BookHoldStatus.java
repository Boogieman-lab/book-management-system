package com.example.bookmanagementsystembo.bookHold.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum BookHoldStatus {
    AVAILABLE("대출 가능"),
    LOANED("대출 중"),
    LOST("분실"),
    DISCARDED("폐기됨"),
    RESERVED("예약 중");
    private final String description;
}
