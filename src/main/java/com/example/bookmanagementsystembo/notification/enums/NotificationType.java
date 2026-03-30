package com.example.bookmanagementsystembo.notification.enums;

public enum NotificationType {
    BORROW_DUE_SOON("반납 기한 임박"),
    BORROW_OVERDUE("반납 기한 초과"),
    RESERVATION_ARRIVED("예약 도서 입고"),
    RESERVATION_EXPIRED("예약 기간 만료"),
    BOOK_REQUEST_APPROVED("희망 도서 신청 승인"),
    BOOK_REQUEST_REJECTED("희망 도서 신청 반려"),
    BOOK_REQUEST_ARRIVED("희망 도서 입고");

    private final String description;

    NotificationType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
