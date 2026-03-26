package com.example.bookmanagementsystembo.bookRequest.dto;

import com.example.bookmanagementsystembo.bookRequest.enums.BookRequestStatus;

public record BookRequestStatusUpdateRequest(BookRequestStatus status, String rejectReason) {
}
