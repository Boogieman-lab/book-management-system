package com.example.bookmanagementsystembo.bookBorrow.entity;

import com.example.bookmanagementsystembo.bookBorrow.enums.BorrowStatus;
import com.example.bookmanagementsystembo.common.entity.BaseEntity;
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
 * 도서 대출 이력 엔티티.
 * 대출 시 borrowDate, 반납 예정일 dueDate, 실제 반납일 returnDate를 기록합니다.
 * 대출 상태(BorrowStatus)로 BORROWED / RETURNED / OVERDUE를 구분합니다.
 * Soft Delete 적용.
 */
@SQLDelete(sql = "UPDATE book_borrow SET is_deleted = true WHERE book_borrow_id = ?")
@Where(clause = "is_deleted = false")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "book_borrow")
public class BookBorrow extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_borrow_id")
    @Comment("대출 ID")
    private Long bookBorrowId;

    @Column(name = "book_hold_id", nullable = false)
    @Comment("도서 보유 ID")
    private Long bookHoldId;

    @Column(name = "book_id", nullable = false)
    @Comment("도서 ID")
    private Long bookId;

    @Column(name = "user_id", nullable = false)
    @Comment("사용자 ID")
    private Long userId;

    @Column(name = "reason", length = 512)
    @Comment("대출 사유")
    private String reason;

    /** 대출 상태: BORROWED(대출 중) / RETURNED(반납 완료) / OVERDUE(연체) */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    @Comment("대출 상태")
    private BorrowStatus status;

    /** 실제 대출 시작 일시 */
    @Column(name = "borrow_date")
    @Comment("대출일시")
    private LocalDateTime borrowDate;

    /** 반납 예정 일시 (정책에 따라 결정) */
    @Column(name = "due_date")
    @Comment("반납예정일")
    private LocalDateTime dueDate;

    /** 실제 반납 완료 일시 (반납 처리 시 설정) */
    @Column(name = "return_date")
    @Comment("반납일시")
    private LocalDateTime returnDate;

    public static BookBorrow create(Long bookHoldId, Long bookId, Long userId, String reason) {
        LocalDateTime now = LocalDateTime.now();
        return new BookBorrow(null, bookHoldId, bookId, userId, reason, BorrowStatus.BORROWED,
                now, now.plusDays(14), null);
    }

    public void updateStatus(BorrowStatus status) {
        this.status = status;
    }

    /** 반납 처리: 상태를 RETURNED로 변경하고 반납일시를 기록합니다. */
    public void returnBook() {
        this.status = BorrowStatus.RETURNED;
        this.returnDate = LocalDateTime.now();
    }
}
