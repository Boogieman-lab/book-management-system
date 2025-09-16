package com.example.bookmanagementsystembo.book.entity;

import com.example.bookmanagementsystembo.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

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

    @Column(name = "status", nullable = false, length = 50)
    @Comment("대출 상태")
    private String status;

    public BookBorrow(Long bookHoldId, Long userId, String reason, String status) {
        this.bookHoldId = bookHoldId;
        this.userId = userId;
        this.reason = reason;
        this.status = status;
    }
}
