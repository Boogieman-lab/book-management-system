package com.example.bookmanagementsystembo.notification.service;

import com.example.bookmanagementsystembo.exception.CoreException;
import com.example.bookmanagementsystembo.exception.ErrorType;
import com.example.bookmanagementsystembo.notification.dto.NotificationPageResponse;
import com.example.bookmanagementsystembo.notification.dto.NotificationResponse;
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

    /**
     * 내 알림 목록을 페이지네이션으로 조회합니다.
     * @param unreadOnly true이면 읽지 않은 알림만 반환합니다.
     */
    public NotificationPageResponse getMyNotifications(Long userId, int page, int size, boolean unreadOnly) {
        long offset = (long) (page - 1) * size;

        List<Notification> notifications = notificationQueryRepository.findByUserId(userId, unreadOnly, offset, size);
        long totalElements = notificationQueryRepository.countByUserId(userId, unreadOnly);

        List<NotificationResponse> items = notifications.stream()
                .map(NotificationResponse::from)
                .toList();

        return NotificationPageResponse.of(items, totalElements, page, size);
    }

    /**
     * 알림을 읽음 처리합니다.
     * IDOR 검증 — 본인의 알림만 읽음 처리 가능합니다.
     * 이미 읽은 알림에 대해서는 멱등하게 동작합니다.
     */
    @Transactional
    public void markAsRead(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new CoreException(ErrorType.NOTIFICATION_NOT_FOUND, notificationId));

        if (!notification.getUserId().equals(userId)) {
            throw new CoreException(ErrorType.NOTIFICATION_NOT_OWNER, notificationId);
        }

        notification.markAsRead();
    }

    /**
     * 현재 사용자의 미읽음 알림을 전체 읽음 처리합니다.
     * 단일 UPDATE 쿼리로 N+1 없이 일괄 처리합니다.
     *
     * @param userId 현재 로그인 사용자 ID
     */
    @Transactional
    public void markAllAsRead(Long userId) {
        notificationRepository.markAllAsReadByUserId(userId);
    }
}
