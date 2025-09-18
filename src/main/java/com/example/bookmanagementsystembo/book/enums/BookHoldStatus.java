package com.example.bookmanagementsystembo.book.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BookHoldStatus {
    AVAILABLE("보관 중, 대출 가능"),
    BORROWED("대출 중"),
    LOST("분실"),
    DISCARDED("폐기");

    private final String desc;
}
