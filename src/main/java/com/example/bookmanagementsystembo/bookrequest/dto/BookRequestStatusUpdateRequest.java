package com.example.bookmanagementsystembo.bookRequest.dto;

import com.example.bookmanagementsystembo.bookRequest.enums.BookRequestStatus;
import jakarta.validation.constraints.NotNull;

public record BookRequestStatusUpdateRequest(@NotNull BookRequestStatus status, String rejectReason) {
}
