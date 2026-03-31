package com.example.bookmanagementsystembo.notification.repository;

import com.example.bookmanagementsystembo.notification.entity.Notification;
import com.example.bookmanagementsystembo.notification.enums.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /** 동일 타입·relatedId·기간 내 알림 발송 여부를 확인합니다 (중복 발송 방지용). */
    boolean existsByTypeAndRelatedIdAndCreatedAtBetween(
            NotificationType type, Long relatedId,
            LocalDateTime from, LocalDateTime to);

    /**
     * 특정 사용자의 미읽음 알림을 전체 읽음 처리합니다.
     *
     * @param userId 대상 사용자 ID
     * @return 업데이트된 행 수
     */
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.userId = :userId AND n.isRead = false")
    int markAllAsReadByUserId(@Param("userId") Long userId);
}
