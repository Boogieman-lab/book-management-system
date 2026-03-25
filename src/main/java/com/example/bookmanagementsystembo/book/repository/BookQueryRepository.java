package com.example.bookmanagementsystembo.book.repository;

import com.example.bookmanagementsystembo.book.dto.BookHoldCountDto;
import com.example.bookmanagementsystembo.book.dto.BookSearchCond;
import com.example.bookmanagementsystembo.book.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
 * QueryDSL 기반 도서 동적 검색 레포지토리.
 * 제목(TITLE), 저자(AUTHOR), ISBN, 출판사(PUBLISHER) 조건별 동적 쿼리와
 * 페이지네이션, 가나다 정렬을 지원합니다.
 */
public interface BookQueryRepository {
    Page<Book> searchBooks(BookSearchCond cond, Pageable pageable);

    /**
     * 도서 ID 목록에 대한 book_hold 수량을 단일 GROUP BY 쿼리로 일괄 조회.
     * N+1 방지용 — 도서 목록 조회 시 사용합니다.
     */
    Map<Long, BookHoldCountDto> countHoldsByBookIds(List<Long> bookIds);
}
