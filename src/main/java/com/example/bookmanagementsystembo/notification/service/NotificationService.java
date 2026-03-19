package com.example.bookmanagementsystembo.notification.service;

import com.example.bookmanagementsystembo.exception.CoreException;
import com.example.bookmanagementsystembo.exception.ErrorType;
import com.example.bookmanagementsystembo.notification.dto.NotificationPageRes;
import com.example.bookmanagementsystembo.notification.dto.NotificationRes;
import com.example.bookmanagementsystembo.notification.entity.Notification;
import com.example.bookmanagementsystembo.notification.repository.NotificationQueryRepository;
import com.example.bookmanagementsystembo.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationQueryRepository notificationQueryRepository;

    public NotificationPageRes getMyNotifications(Long userId, int page, int size, boolean unreadOnly) {
        long offset = (long) (page - 1) * size;

        List<Notification> notifications = notificationQueryRepository.findByUserId(userId, unreadOnly, offset, size);
        long totalElements = notificationQueryRepository.countByUserId(userId, unreadOnly);

        List<NotificationRes> items = notifications.stream()
                .map(NotificationRes::from)
                .toList();

        return NotificationPageRes.of(items, totalElements, page, size);
    }

    @Transactional
    public void markAsRead(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new CoreException(ErrorType.NOTIFICATION_NOT_FOUND, notificationId));

        if (!notification.getUserId().equals(userId)) {
            throw new CoreException(ErrorType.NOTIFICATION_NOT_OWNER, notificationId);
        }

        notification.markAsRead();
    }
}
