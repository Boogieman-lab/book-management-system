package com.bms.admin.book;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
//@Table(name = "menu_tbl")
public class BookEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id; // 책 ID (기본 키)

    @Column(name = "title", nullable = false, length = 255)
    private String title; // 책 제목

    @Column(name = "author", nullable = false, length = 255)
    private String author; // 저자

    @Column(name = "publisher", length = 255)
    private String publisher; // 출판사

    @Column(name = "pub_date", length = 20)
    private String pubDate; // 출판일

    @Column(name = "description", columnDefinition = "TEXT")
    private String description; // 설명
}
