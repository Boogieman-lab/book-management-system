package com.example.bookmanagementsystembo.notification.dto;

import com.example.bookmanagementsystembo.notification.entity.Notification;
import com.example.bookmanagementsystembo.notification.enums.NotificationType;

import java.time.LocalDateTime;

public record NotificationResponse(
        Long notificationId,
        NotificationType type,
        String message,
        Boolean isRead,
        Long relatedId,
        LocalDateTime createdAt
) {
    public static NotificationResponse from(Notification notification) {
        return new NotificationResponse(
                notification.getNotificationId(),
                notification.getType(),
                notification.getMessage(),
                notification.getIsRead(),
                notification.getRelatedId(),
                notification.getCreatedAt()
        );
    }
}
