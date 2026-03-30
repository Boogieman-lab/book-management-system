package com.example.bookmanagementsystembo.bookRequest.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BookRequestStatus {
    PENDING("대기"),
    APPROVED("승인"),
    REJECTED("거절"),
    ARRIVED("입고");

    private final String desc;
}
