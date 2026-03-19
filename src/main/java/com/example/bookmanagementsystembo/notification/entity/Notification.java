package com.example.bookmanagementsystembo.notification.entity;

import com.example.bookmanagementsystembo.common.entity.BaseEntity;
import com.example.bookmanagementsystembo.notification.enums.NotificationType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@SQLDelete(sql = "UPDATE notification SET is_deleted = true WHERE notification_id = ?")
@Where(clause = "is_deleted = false")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "notification")
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    @Comment("알림 ID")
    private Long notificationId;

    @Column(name = "user_id", nullable = false)
    @Comment("수신자 ID")
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 50)
    @Comment("알림 유형")
    private NotificationType type;

    @Column(name = "message", nullable = false, length = 512)
    @Comment("알림 내용")
    private String message;

    @Column(name = "is_read", nullable = false)
    @Comment("읽음 여부")
    private Boolean isRead = false;

    @Column(name = "related_id")
    @Comment("관련 엔티티 ID")
    private Long relatedId;

    public void markAsRead() {
        this.isRead = true;
    }

    public static Notification create(Long userId, NotificationType type, String message, Long relatedId) {
        return new Notification(null, userId, type, message, false, relatedId);
    }
}
