package com.example.bookmanagementsystembo.notification.dto;

import com.example.bookmanagementsystembo.notification.entity.Notification;
import com.example.bookmanagementsystembo.notification.enums.NotificationType;

import java.time.LocalDateTime;

public record NotificationRes(
        Long notificationId,
        NotificationType type,
        String message,
        Boolean isRead,
        Long relatedId,
        LocalDateTime createdAt
) {
    public static NotificationRes from(Notification notification) {
        return new NotificationRes(
                notification.getNotificationId(),
                notification.getType(),
                notification.getMessage(),
                notification.getIsRead(),
                notification.getRelatedId(),
                notification.getCreatedAt()
        );
    }
}
