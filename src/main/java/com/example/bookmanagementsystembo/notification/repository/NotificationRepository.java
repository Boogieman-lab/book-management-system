package com.example.bookmanagementsystembo.notification.repository;

import com.example.bookmanagementsystembo.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
