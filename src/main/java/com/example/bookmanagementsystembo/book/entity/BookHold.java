package com.example.bookmanagementsystembo.book.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name ="book_hold")
public class BookHold {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_hold_id")
    @Comment("도서 보유 ID")
    private Long bookHoldId;

    @Column(name = "book_id", nullable = false)
    @Comment("도서 ID")
    private Long bookId;

    @Column(name = "title", nullable = false, length = 512)
    @Comment("도서명")
    private String title;

    @Column(name = "status", nullable = false, length = 50)
    @Comment("보유 상태")
    private String status;

    @Column(name = "location", length = 50)
    @Comment("위치")
    private String location;

    public BookHold(Long bookId, String title, String status, String location) {
        this.bookId = bookId;
        this.title = title;
        this.status = status;
        this.location = location;
    }
}
