package com.example.bookmanagementsystembo.book.entity;

import com.example.bookmanagementsystembo.book.dto.BookCreateRequest;
import com.example.bookmanagementsystembo.book.dto.BookUpdateRequest;
import com.example.bookmanagementsystembo.book.utils.JsonUtils;
import com.example.bookmanagementsystembo.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

/**
 * 도서 메타 정보 엔티티.
 * 카카오 도서 API에서 가져온 도서 정보를 저장하며,
 * 실물 재고는 BookHold 엔티티에서 별도 관리합니다.
 * Soft Delete 적용.
 */
@SQLDelete(sql = "UPDATE book SET is_deleted = true WHERE book_id = ?")
@Where(clause = "is_deleted = false")
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

    @Column(name = "published_at")
    @Comment("출판일")
    private LocalDateTime publishedAt;

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

    /** 카카오 API 기준 판매 상태 (예: 정상판매, 판매중지, 품절 등) */
    @Column(name = "status", length = 50)
    @Comment("판매 상태")
    private String status;

    public static Book create(BookCreateRequest dto) {
        return new Book(
                null,
                dto.title() ,
                dto.contents(),
                dto.url(),
                dto.isbn(),
                dto.publishedAt(),
                JsonUtils.toJson(dto.authors()),
                JsonUtils.toJson(dto.translators()),
                dto.publisher(),
                dto.price(),
                dto.salePrice(),
                dto.thumbnail(),
                dto.status()
        );
    }

    public void update(BookUpdateRequest dto) {
        this.title = dto.title();
        this.contents = dto.contents();
        this.url = dto.url();
        this.authors = JsonUtils.toJson(dto.authors());
        this.translators = JsonUtils.toJson(dto.translators());
        this.publisher = dto.publisher();
        this.price = dto.price();
        this.salePrice = dto.salePrice();
        this.thumbnail = dto.thumbnail();
    }

}