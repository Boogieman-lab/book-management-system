package com.example.bookmanagementsystembo.notification.service;

import com.example.bookmanagementsystembo.exception.CoreException;
import com.example.bookmanagementsystembo.exception.ErrorType;
import com.example.bookmanagementsystembo.notification.dto.NotificationPageResponse;
import com.example.bookmanagementsystembo.notification.dto.NotificationResponse;
import com.example.bookmanagementsystembo.notification.entity.Notification;
import com.example.bookmanagementsystembo.notification.enums.NotificationType;
import com.example.bookmanagementsystembo.notification.repository.NotificationQueryRepository;
import com.example.bookmanagementsystembo.notification.repository.NotificationRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationQueryRepository notificationQueryRepository;
    private final SseEmitterManager sseEmitterManager;
    private final NotificationRedisPublisher redisPublisher;
    private final ObjectMapper objectMapper;

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

    /**
     * SSE 구독을 등록합니다.
     *
     * @param userId 구독할 사용자 ID
     * @return SseEmitter
     */
    public SseEmitter subscribe(Long userId) {
        return sseEmitterManager.subscribe(userId);
    }

    /**
     * 알림을 DB에 저장하고 트랜잭션 커밋 후 SSE로 실시간 전송합니다.
     * 커밋 이후에 SSE를 전송하므로 롤백 시 ghost 알림이 발송되지 않습니다.
     *
     * @param userId    수신자 ID
     * @param type      알림 유형
     * @param message   알림 메시지
     * @param relatedId 관련 엔티티 ID (nullable)
     */
    @Transactional
    public void saveAndSend(Long userId, NotificationType type, String message, Long relatedId) {
        Notification notification = Notification.create(userId, type, message, relatedId);
        notificationRepository.save(notification);

        NotificationResponse response = NotificationResponse.from(notification);
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    publishToRedis(userId, response);
                }
            });
        } else {
            publishToRedis(userId, response);
        }
    }

    /**
     * 오늘 이미 동일 타입·relatedId로 알림이 발송되었는지 확인합니다 (중복 발송 방지용).
     *
     * @param type      알림 유형
     * @param relatedId 관련 엔티티 ID
     * @return 오늘 발송 이력이 있으면 true
     */
    public boolean alreadySentToday(NotificationType type, Long relatedId) {
        LocalDateTime from = LocalDate.now().atStartOfDay();
        LocalDateTime to   = from.plusDays(1);
        return notificationRepository.existsByTypeAndRelatedIdAndCreatedAtBetween(type, relatedId, from, to);
    }

    /**
     * 알림을 Redis 채널로 발행합니다.
     * Redis 발행 실패 시 단일 인스턴스 환경 폴백으로 직접 SSE 전송합니다.
     */
    private void publishToRedis(Long userId, NotificationResponse response) {
        try {
            String payload = objectMapper.writeValueAsString(response);
            redisPublisher.publish(userId, payload);
        } catch (JsonProcessingException e) {
            log.warn("알림 JSON 직렬화 실패 — userId={}, 직접 SSE 전송으로 폴백", userId, e);
            sseEmitterManager.sendToUser(userId, response);
        }
    }
}
