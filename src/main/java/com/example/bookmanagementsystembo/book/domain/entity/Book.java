package com.example.bookmanagementsystembo.book.domain.entity;

import com.example.bookmanagementsystembo.book.domain.Utils;
import com.example.bookmanagementsystembo.book.domain.dto.BookCreateDto;
import com.example.bookmanagementsystembo.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "book")
public class Book extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_id")
    @Comment("도서 ID")
    private Long bookId;

    @Column(name = "title", nullable = false, length = 512)
    @Comment("도서 제목")
    private String title;

    @Column(name = "contents", columnDefinition = "TEXT")
    @Comment("도서 소개")
    private String contents;

    @Column(name = "url", length = 1024)
    @Comment("도서 상세 URL")
    private String url;

    @Column(name = "isbn", length = 50)
    @Comment("ISBN10 또는 ISBN13 (공백 구분)")
    private String isbn;

    @Column(name = "datetime")
    @Comment("출판일")
    private LocalDateTime datetime;

    @Column(name = "authors", columnDefinition = "JSON")
    @Comment("저자 리스트")
    private String authors;

    @Column(name = "translators", columnDefinition = "JSON")
    @Comment("번역자 리스트")
    private String translators;

    @Column(name = "publisher", length = 255)
    @Comment("출판사")
    private String publisher;

    @Column(name = "price", nullable = false)
    @Comment("정가")
    private int price;

    @Column(name = "sale_price", nullable = false)
    @Comment("판매가")
    private int salePrice;

    @Column(name = "thumbnail", length = 512)
    @Comment("표지 이미지 URL")
    private String thumbnail;

    @Column(name = "status", length = 50)
    @Comment("판매 상태")
    private String status;

    public static Book create(BookCreateDto dto) {
        return new Book(
                null,
                dto.title() ,
                dto.contents(),
                dto.url(),
                dto.isbn(),
                dto.datetime(),
                Utils.toJson(dto.authors()),
                Utils.toJson(dto.translators()),
                dto.publisher(),
                dto.price(),
                dto.salePrice(),
                dto.thumbnail(),
                dto.status()
        );
    }

    public void update(String title, List<String> authors, String publisher) {
        if (title != null) this.title = title;
        if (authors != null) this.authors = Utils.toJson(authors);
        if (publisher != null) this.publisher = publisher;
    }
}