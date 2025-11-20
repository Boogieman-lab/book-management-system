package com.example.bookmanagementsystembo.bookBorrow.domain.entity;

import com.example.bookmanagementsystembo.bookBorrow.enums.BorrowStatus;
import com.example.bookmanagementsystembo.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

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

    @Column(name = "user_id", nullable = false)
    @Comment("사용자 ID")
    private Long userId;

    @Column(name = "reason", length = 512)
    @Comment("대출 사유")
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    @Comment("대출 상태")
    private BorrowStatus status;

    public static BookBorrow create(Long bookHoldId, Long userId, String reason) {
        return new BookBorrow(null, bookHoldId, userId, reason, BorrowStatus.BORROWED);
    }

    public void updateStatus(BorrowStatus status) {
        this.status = status;
    }
}
