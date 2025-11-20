package com.example.bookmanagementsystembo.bookBorrow.enums;

import com.example.bookmanagementsystembo.exception.CoreException;
import com.example.bookmanagementsystembo.exception.ErrorType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BorrowStatus {
    BORROWED("대출"),
    RETURNED("반납"),
    OVERDUE("연체");
    private final String desc;

    public static BorrowStatus fromString(String statusString) {
        for (BorrowStatus status : BorrowStatus.values()) {
            if (status.name().equalsIgnoreCase(statusString)) {
                return status;
            }
        }
        throw new CoreException(ErrorType.BORROWSTATUS_NOT_FOUND, statusString);
    }
}
