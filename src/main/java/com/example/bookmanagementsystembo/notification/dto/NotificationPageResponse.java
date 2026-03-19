package com.example.bookmanagementsystembo.notification.dto;

import java.util.List;

public record NotificationPageResponse(
        List<NotificationResponse> items,
        long totalElements,
        int page,
        int size
) {
    public static NotificationPageResponse of(List<NotificationResponse> items, long totalElements, int page, int size) {
        return new NotificationPageResponse(items, totalElements, page, size);
    }
}
