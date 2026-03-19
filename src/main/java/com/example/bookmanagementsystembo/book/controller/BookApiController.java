package com.example.bookmanagementsystembo.book.controller;

import com.example.bookmanagementsystembo.book.dto.BookDetailRes;
import com.example.bookmanagementsystembo.book.dto.BookSearchCond;
import com.example.bookmanagementsystembo.book.dto.BookSummaryRes;
import com.example.bookmanagementsystembo.book.enums.BookSearchField;
import com.example.bookmanagementsystembo.book.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 도서 검색 서비스 V1 API.
 * Base URL: /api/v1/books
 *
 * <p>GET /api/v1/books          — 도서 목록 (페이지네이션 + 동적 검색 + 가나다 정렬)
 * <p>GET /api/v1/books/{bookId} — 도서 상세 (전체 정보 + 실재고 수량)
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/books")
public class BookApiController {

    private final BookService bookService;

    /**
     * 도서 목록 조회.
     *
     * @param keyword 검색어 (선택). 없으면 전체 조회
     * @param field   검색 필드: TITLE | AUTHOR | ISBN | PUBLISHER (선택). 없으면 제목+저자+ISBN 통합 검색
     * @param page    페이지 번호 (0-based, 기본 0)
     * @param size    페이지 크기 (기본 10)
     */
    @GetMapping
    public ResponseEntity<Page<BookSummaryRes>> searchBooks(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) BookSearchField field,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        BookSearchCond cond = new BookSearchCond(keyword, field);
        return ResponseEntity.ok(bookService.searchBooksV1(cond, PageRequest.of(page, size)));
    }

    /**
     * 도서 상세 조회.
     * ISBN, 저술가, 이미지 주소 등 전체 메타 정보와 현재 대출 가능 재고 수량을 반환합니다.
     *
     * @param bookId 도서 ID
     */
    @GetMapping("/{bookId}")
    public ResponseEntity<BookDetailRes> getBookDetail(@PathVariable Long bookId) {
        return ResponseEntity.ok(bookService.getBookDetail(bookId));
    }
}
