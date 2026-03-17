package com.example.bookmanagementsystembo.bookHold.entity;


import com.example.bookmanagementsystembo.bookHold.enums.BookHoldStatus;
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
@Table(name ="book_hold")
public class BookHold extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_hold_id")
    @Comment("도서 보유 ID")
    private Long bookHoldId;

    @Column(name = "book_id", nullable = false)
    @Comment("도서 ID")
    private Long bookId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    @Comment("보유 상태")
    private BookHoldStatus status;

    @Column(name = "location", length = 50)
    @Comment("위치")
    private String location;

    public static BookHold create(Long bookId) {
        return new BookHold(null, bookId, BookHoldStatus.AVAILABLE, null);
    }
}
