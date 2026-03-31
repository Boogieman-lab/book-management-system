package com.example.bookmanagementsystembo.reservation.domain.entity;

import com.example.bookmanagementsystembo.common.entity.BaseEntity;
import com.example.bookmanagementsystembo.exception.CoreException;
import com.example.bookmanagementsystembo.exception.ErrorType;
import com.example.bookmanagementsystembo.reservation.enums.ReservationStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

/**
 * 도서 예약 엔티티.
 * 대출 중인 도서에 대해 사용자가 예약을 걸면 생성됩니다.
 * 예약 상태(ReservationStatus): WAITING(대기) / NOTIFIED(픽업 알림) / RESERVED(수령 완료) / EXPIRED(만료)
 * Soft Delete 적용.
 */
@SQLDelete(sql = "UPDATE reservation SET is_deleted = true WHERE reservation_id = ?")
@Where(clause = "is_deleted = false")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "reservation")
public class Reservation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    @Comment("예약 ID")
    private Long reservationId;

    @Column(name = "book_hold_id", nullable = false)
    @Comment("도서 보유 ID")
    private Long bookHoldId;

    @Column(name = "user_id", nullable = false)
    @Comment("사용자 ID")
    private Long userId;

    /** 예약 상태: WAITING(대기 중) / NOTIFIED(픽업 알림) / RESERVED(수령 완료) / EXPIRED(만료) */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    @Comment("예약 상태")
    private ReservationStatus status;

    /** 예약이 접수된 일시 */
    @Column(name = "reserved_at", nullable = false)
    @Comment("예약일시")
    private LocalDateTime reservedAt;

    /** 예약 만료 일시 (픽업 기한). 이 시각 이후에는 EXPIRED 처리됨 */
    @Column(name = "expire_at")
    @Comment("예약만료일시")
    private LocalDateTime expireAt;

    /** 예약 생성 팩토리 메서드 */
    public static Reservation create(Long bookHoldId, Long userId, LocalDateTime expireAt) {
        return new Reservation(null, bookHoldId, userId, ReservationStatus.WAITING,
                LocalDateTime.now(), expireAt);
    }

    /**
     * 픽업 알림 처리 (WAITING → NOTIFIED). expireAt에 픽업 기한을 설정합니다.
     * WAITING 상태에서만 호출 가능합니다.
     */
    public void notifyPickup(LocalDateTime expireAt) {
        if (this.status != ReservationStatus.WAITING) {
            throw new CoreException(ErrorType.INVALID_RESERVATION_STATUS, this.reservationId);
        }
        this.status = ReservationStatus.NOTIFIED;
        this.expireAt = expireAt;
    }

    /**
     * 대출 수령 완료 처리 (NOTIFIED → RESERVED).
     * NOTIFIED 상태에서만 호출 가능합니다.
     */
    public void reserve() {
        if (this.status != ReservationStatus.NOTIFIED) {
            throw new CoreException(ErrorType.INVALID_RESERVATION_STATUS, this.reservationId);
        }
        this.status = ReservationStatus.RESERVED;
    }

    /**
     * 예약 만료 처리 (WAITING | NOTIFIED → EXPIRED).
     * 이미 EXPIRED이거나 RESERVED 상태이면 예외를 발생시킵니다.
     */
    public void expire() {
        if (this.status == ReservationStatus.EXPIRED || this.status == ReservationStatus.RESERVED) {
            throw new CoreException(ErrorType.INVALID_RESERVATION_STATUS, this.reservationId);
        }
        this.status = ReservationStatus.EXPIRED;
    }
}
