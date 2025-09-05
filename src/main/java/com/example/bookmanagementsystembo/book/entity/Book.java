package com.example.bookmanagementsystembo.book.entity;

import com.fasterxml.jackson.core.type.TypeReference;
import com.example.bookmanagementsystembo.user.domain.entity.BaseEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
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
    @Comment("도서명")
    private String title;

    @Column(name = "authors", length = 512)
    @Comment("저자")
    private String authors;

    @Column(name = "publisher", length = 255)
    @Comment("출판사")
    private String publisher;

    @Column(name = "isbn", length = 50, unique = true)
    @Comment("국제 표준 도서번호")
    private String isbn;

    @Column(name = "pub_date")
    @Comment("출간일")
    private LocalDate pubDate;

    @Column(name = "page_count")
    @Comment("페이지 수")
    private Integer pageCount;

    @Column(name = "genre", length = 50)
    @Comment("장르")
    private String genre;

    @Column(name = "image_url", length = 512)
    @Comment("표지 이미지")
    private String imageUrl;

    // authors를 List<String>으로 변환
    public List<String> getAuthorsAsList(ObjectMapper objectMapper) {
        try {
            return objectMapper.readValue(this.authors, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    // List<String>을 JSON 문자열로 변환해 저장
    public void setAuthorsFromList(List<String> authorsList, ObjectMapper objectMapper) {
        try {
            this.authors = objectMapper.writeValueAsString(authorsList);
        } catch (Exception e) {
            this.authors = "[]"; // 실패하면 빈 배열로 초기화
        }
    }

}