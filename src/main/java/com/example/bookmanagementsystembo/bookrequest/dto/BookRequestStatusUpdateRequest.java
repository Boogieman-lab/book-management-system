package com.example.bookmanagementsystembo.bookRequest.dto;

public record BookRequestStatusUpdateRequest(BookRequestStatus status, String rejectReason) {
}
