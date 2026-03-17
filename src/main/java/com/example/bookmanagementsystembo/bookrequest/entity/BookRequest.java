package com.example.bookmanagementsystembo.bookrequest.entity;

import com.example.bookmanagementsystembo.bookrequest.dto.BookRequestStatus;
import com.example.bookmanagementsystembo.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Entity
@Table(name = "book_request")
public class BookRequest extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_request_id")
    @Comment("도서 희망 신청 ID")
    private Long bookRequestId;

    @Column(name = "user_id", nullable = false)
    @Comment("사용자 ID")
    private Long userId;

    @Column(name = "title", nullable = false, length = 512)
    @Comment("도서 제목")
    private String title;

    @Column(name = "authors", length = 255)
    @Comment("저자 리스트")
    private String authors;

    @Column(name = "publisher", length = 255)
    @Comment("출판사")
    private String publisher;

    @Column(name = "isbn", length = 50)
    @Comment("ISBN10 또는 ISBN13 (공백 구분)")
    private String isbn;

    @Column(name = "reason", length = 512)
    @Comment("신청 사유")
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @Comment("책 신청 싱태")
    private BookRequestStatus status;

    public static BookRequest create(
            Long userId,
            String title,
            String authors,
            String publisher,
            String isbn,
            String reason) {
        return new BookRequest(
                null,
                userId,
                title,
                authors,
                publisher,
                isbn,
                reason,
                BookRequestStatus.PENDING
        );
    }

    public void update(String title, String authors, String publisher, String isbn, String reason) {
        this.title = title;
        this.authors = authors;
        this.publisher = publisher;
        this.isbn = isbn;
        this.reason = reason;
    }
}
