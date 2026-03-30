package com.example.bookmanagementsystembo.reservation.domain.entity;

import com.example.bookmanagementsystembo.bookHold.entity.BookHold;
import com.example.bookmanagementsystembo.common.entity.BaseEntity;
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
 * 예약 상태(ReservationStatus): WAITING(대기) / EXPIRED(만료)
 * - WAITING  : 반납 후 픽업 대기 중
 * - EXPIRED  : 픽업 기한(expireAt) 초과 또는 자동 취소된 상태
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

    /**
     * 예약 대상 도서 보유본 (BookHold).
     * 지연 로딩으로 불필요한 JOIN 방지.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_hold_id", nullable = false)
    @Comment("도서 보유 ID")
    private BookHold bookHold;

    @Column(name = "user_id", nullable = false)
    @Comment("사용자 ID")
    private Long userId;

    /** 예약 상태: WAITING(대기 중) / EXPIRED(만료) */
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
    public static Reservation create(BookHold bookHold, Long userId, LocalDateTime expireAt) {
        return new Reservation(null, bookHold, userId, ReservationStatus.WAITING,
                LocalDateTime.now(), expireAt);
    }

    /** 픽업 알림 처리 (WAITING → NOTIFIED). expireAt에 픽업 기한을 설정합니다. */
    public void notifyPickup(LocalDateTime expireAt) {
        this.status = ReservationStatus.NOTIFIED;
        this.expireAt = expireAt;
    }

    /** 대출 수령 완료 처리 (NOTIFIED → RESERVED) */
    public void reserve() {
        this.status = ReservationStatus.RESERVED;
    }

    /** 예약 만료 처리 */
    public void expire() {
        this.status = ReservationStatus.EXPIRED;
    }
}
