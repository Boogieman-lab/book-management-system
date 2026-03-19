package com.example.bookmanagementsystembo.notification.repository;

import com.example.bookmanagementsystembo.notification.entity.Notification;

import java.util.List;

public interface NotificationQueryRepository {
    List<Notification> findByUserId(Long userId, boolean unreadOnly, long offset, int size);

    long countByUserId(Long userId, boolean unreadOnly);
}
