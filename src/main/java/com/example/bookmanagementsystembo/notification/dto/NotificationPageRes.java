package com.example.bookmanagementsystembo.notification.dto;

import java.util.List;

public record NotificationPageRes(
        List<NotificationRes> items,
        long totalElements,
        int page,
        int size
) {
    public static NotificationPageRes of(List<NotificationRes> items, long totalElements, int page, int size) {
        return new NotificationPageRes(items, totalElements, page, size);
    }
}
