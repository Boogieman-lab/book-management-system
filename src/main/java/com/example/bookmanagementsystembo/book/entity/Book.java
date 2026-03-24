package com.example.bookmanagementsystembo.book.entity;

import com.example.bookmanagementsystembo.book.dto.BookCreateRequest;
import com.example.bookmanagementsystembo.book.dto.BookUpdateRequest;
import com.example.bookmanagementsystembo.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDate;

/**
 * 도서 메타 정보 엔티티.
 * 알라딘 도서 API 기반 스키마를 사용하며,
 * 실물 재고는 BookHold 엔티티에서 별도 관리합니다.
 * Soft Delete 적용.
 */
@SQLDelete(sql = "UPDATE book SET is_deleted = true WHERE book_id = ?")
@Where(clause = "is_deleted = false")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "book")
public class Book extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_id")
    @Comment("도서 내부 식별 ID")
    private Long bookId;

    @Column(name = "isbn13", nullable = false, length = 13, unique = true)
    @Comment("ISBN13 (숫자 13자리, 하이픈 제거)")
    private String isbn13;

    @Column(name = "isbn10", length = 10)
    @Comment("ISBN10")
    private String isbn10;

    @Column(name = "title", nullable = false, length = 512)
    @Comment("도서 제목")
    private String title;

    @Column(name = "author", nullable = false, length = 512)
    @Comment("저자/아티스트 정보")
    private String author;

    @Column(name = "publisher", nullable = false, length = 255)
    @Comment("출판사")
    private String publisher;

    @Column(name = "pub_date")
    @Comment("출간일")
    private LocalDate pubDate;

    @Column(name = "description", columnDefinition = "TEXT")
    @Comment("상품 설명 (요약)")
    private String description;

    @Column(name = "cover_url", length = 1024)
    @Comment("표지 이미지 URL (cover500 규격 권장)")
    private String coverUrl;

    @Column(name = "category_id")
    @Comment("알라딘 분야 ID")
    private Integer categoryId;

    @Column(name = "category_name", length = 255)
    @Comment("분야명")
    private String categoryName;

    @Column(name = "price_standard")
    @Comment("정가")
    private int priceStandard;

    @Column(name = "price_sales")
    @Comment("판매가")
    private int priceSales;

    @Column(name = "stock_status", length = 50)
    @Comment("재고 상태 (품절, 절판 등)")
    private String stockStatus;

    @Column(name = "customer_review_rank")
    @Comment("회원 리뷰 평점 (0~10)")
    private int customerReviewRank;

    @Column(name = "series_id")
    @Comment("시리즈 ID")
    private Integer seriesId;

    @Column(name = "series_name", length = 255)
    @Comment("시리즈 이름")
    private String seriesName;

    @Column(name = "mall_type", length = 20)
    @Comment("상품 타입 (BOOK, MUSIC 등)")
    private String mallType;

    @Column(name = "adult_yn", length = 1)
    @Comment("성인 등급 여부 (Y/N)")
    private String adultYn;

    public static Book create(BookCreateRequest dto) {
        Book book = new Book();
        book.isbn13 = dto.isbn13();
        book.isbn10 = dto.isbn10();
        book.title = dto.title();
        book.author = dto.author();
        book.publisher = dto.publisher();
        book.pubDate = dto.pubDate();
        book.description = dto.description();
        book.coverUrl = dto.coverUrl();
        book.categoryId = dto.categoryId();
        book.categoryName = dto.categoryName();
        book.priceStandard = dto.priceStandard();
        book.priceSales = dto.priceSales();
        book.stockStatus = dto.stockStatus();
        book.customerReviewRank = dto.customerReviewRank();
        book.seriesId = dto.seriesId();
        book.seriesName = dto.seriesName();
        book.mallType = dto.mallType() != null ? dto.mallType() : "BOOK";
        book.adultYn = dto.adultYn() != null ? dto.adultYn() : "N";
        return book;
    }

    public void update(BookUpdateRequest dto) {
        this.title = dto.title();
        this.author = dto.author();
        this.publisher = dto.publisher();
        this.description = dto.description();
        this.coverUrl = dto.coverUrl();
        this.priceStandard = dto.priceStandard();
        this.priceSales = dto.priceSales();
        this.stockStatus = dto.stockStatus();
    }
}
