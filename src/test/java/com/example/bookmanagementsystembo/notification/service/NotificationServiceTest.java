package com.example.bookmanagementsystembo.notification.service;

import com.example.bookmanagementsystembo.exception.CoreException;
import com.example.bookmanagementsystembo.exception.ErrorType;
import com.example.bookmanagementsystembo.notification.dto.NotificationPageRes;
import com.example.bookmanagementsystembo.notification.entity.Notification;
import com.example.bookmanagementsystembo.notification.enums.NotificationType;
import com.example.bookmanagementsystembo.notification.repository.NotificationQueryRepository;
import com.example.bookmanagementsystembo.notification.repository.NotificationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @InjectMocks
    private NotificationService notificationService;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private NotificationQueryRepository notificationQueryRepository;

    @Test
    @DisplayName("내 알림 조회 성공 - 전체")
    void getMyNotifications_success() {
        // Given
        Long userId = 1L;
        Notification n1 = Notification.create(userId, NotificationType.BORROW_DUE_SOON, "반납 예정일입니다.", 100L);
        Notification n2 = Notification.create(userId, NotificationType.BORROW_OVERDUE, "연체가 발생했습니다.", 101L);

        when(notificationQueryRepository.findByUserId(userId, false, 0, 10)).thenReturn(List.of(n1, n2));
        when(notificationQueryRepository.countByUserId(userId, false)).thenReturn(2L);

        // When
        NotificationPageRes result = notificationService.getMyNotifications(userId, 1, 10, false);

        // Then
        assertThat(result.items()).hasSize(2);
        assertThat(result.totalElements()).isEqualTo(2);
        assertThat(result.page()).isEqualTo(1);
        assertThat(result.size()).isEqualTo(10);
        verify(notificationQueryRepository).findByUserId(userId, false, 0, 10);
        verify(notificationQueryRepository).countByUserId(userId, false);
    }

    @Test
    @DisplayName("내 알림 조회 - unreadOnly 필터")
    void getMyNotifications_unreadOnly() {
        // Given
        Long userId = 1L;
        Notification n1 = Notification.create(userId, NotificationType.RESERVATION_ARRIVED, "예약 도서가 도착했습니다.", 200L);

        when(notificationQueryRepository.findByUserId(userId, true, 0, 10)).thenReturn(List.of(n1));
        when(notificationQueryRepository.countByUserId(userId, true)).thenReturn(1L);

        // When
        NotificationPageRes result = notificationService.getMyNotifications(userId, 1, 10, true);

        // Then
        assertThat(result.items()).hasSize(1);
        assertThat(result.totalElements()).isEqualTo(1);
        verify(notificationQueryRepository).findByUserId(userId, true, 0, 10);
    }

    @Test
    @DisplayName("읽음 처리 성공")
    void markAsRead_success() {
        // Given
        Long notificationId = 1L;
        Long userId = 1L;
        Notification notification = Notification.create(userId, NotificationType.BOOK_REQUEST_APPROVED, "희망 도서가 승인되었습니다.", 300L);

        when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(notification));

        // When
        notificationService.markAsRead(notificationId, userId);

        // Then
        assertThat(notification.getIsRead()).isTrue();
    }

    @Test
    @DisplayName("이미 읽은 알림 읽음 처리 - 멱등")
    void markAsRead_alreadyRead_idempotent() {
        // Given
        Long notificationId = 1L;
        Long userId = 1L;
        Notification notification = Notification.create(userId, NotificationType.RESERVATION_EXPIRED, "예약이 만료되었습니다.", 400L);
        notification.markAsRead();

        when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(notification));

        // When
        notificationService.markAsRead(notificationId, userId);

        // Then
        assertThat(notification.getIsRead()).isTrue();
    }

    @Test
    @DisplayName("타인 알림 읽음 처리 - IDOR 차단 (403)")
    void markAsRead_notOwner_forbidden() {
        // Given
        Long notificationId = 1L;
        Long ownerId = 1L;
        Long attackerId = 2L;
        Notification notification = Notification.create(ownerId, NotificationType.BORROW_OVERDUE, "연체가 발생했습니다.", 500L);

        when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(notification));

        // When & Then
        CoreException ex = assertThrows(CoreException.class,
                () -> notificationService.markAsRead(notificationId, attackerId));
        assertEquals(ErrorType.NOTIFICATION_NOT_OWNER, ex.getErrorType());
    }

    @Test
    @DisplayName("알림 없음 - 404")
    void markAsRead_notFound() {
        // Given
        Long notificationId = 999L;
        Long userId = 1L;

        when(notificationRepository.findById(notificationId)).thenReturn(Optional.empty());

        // When & Then
        CoreException ex = assertThrows(CoreException.class,
                () -> notificationService.markAsRead(notificationId, userId));
        assertEquals(ErrorType.NOTIFICATION_NOT_FOUND, ex.getErrorType());
    }
}
