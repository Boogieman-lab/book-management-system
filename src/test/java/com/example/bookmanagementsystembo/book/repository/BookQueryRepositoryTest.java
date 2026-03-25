package com.example.bookmanagementsystembo.book.repository;

import com.example.bookmanagementsystembo.book.dto.BookSearchCond;
import com.example.bookmanagementsystembo.book.entity.Book;
import com.example.bookmanagementsystembo.book.enums.BookSearchField;
import com.example.bookmanagementsystembo.book.service.BookService;
import com.example.bookmanagementsystembo.book.dto.BookCreateRequest;
import com.example.bookmanagementsystembo.book.dto.BookDetailResponse;
import com.example.bookmanagementsystembo.book.dto.BookSummaryResponse;
import com.example.bookmanagementsystembo.bookHold.enums.BookHoldStatus;
import com.example.bookmanagementsystembo.bookHold.repository.BookHoldRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * BookQueryRepositoryImpl + BookService 통합 테스트.
 *
 * <p>⚠️ 실행 전 Docker 서비스(MariaDB, Redis)가 기동되어 있어야 합니다:
 * <pre>docker-compose up -d</pre>
 *
 * <p>@Transactional로 각 테스트 종료 후 자동 롤백합니다.
 */
@SpringBootTest
@Transactional
@DisplayName("BookQueryRepository + BookService 통합 테스트")
class BookQueryRepositoryTest {

    @Autowired
    private BookService bookService;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookHoldRepository bookHoldRepository;

    @Autowired
    private BookQueryRepository bookQueryRepository;

    /** 테스트용 도서 3권을 미리 등록 */
    @BeforeEach
    void setUp() {
        // 각 테스트 격리: 기존 데이터와 독립적으로 생성
        bookService.create(sampleReq("이펙티브 자바 3판", "9788966262281", "조슈아 블로크", "인사이트"));
        bookService.create(sampleReq("스프링 인 액션",    "9791162241769", "크레이그 월스",  "제이펍"));
        bookService.create(sampleReq("자바 ORM 표준 JPA", "9788960777330", "김영한",        "에이콘출판사"));
    }

    // ──────────────────────────────────────────────────────────────
    // 동적 검색 조건
    // ──────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("동적 검색 조건")
    class DynamicSearchTest {

        @Test
        @DisplayName("키워드 없이 전체 조회하면 등록된 모든 도서가 반환된다")
        void noKeyword_returnsAll() {
            BookSearchCond cond = new BookSearchCond(null, null);

            Page<Book> result = bookQueryRepository.searchBooks(cond, PageRequest.of(0, 20));

            // setUp에서 등록한 3권 이상 존재
            assertThat(result.getTotalElements()).isGreaterThanOrEqualTo(3);
        }

        @Test
        @DisplayName("제목 키워드 '자바'로 검색하면 제목에 '자바'가 포함된 도서만 반환된다")
        void titleKeyword_returnsOnlyMatchingBooks() {
            BookSearchCond cond = new BookSearchCond("자바", BookSearchField.TITLE);

            Page<Book> result = bookQueryRepository.searchBooks(cond, PageRequest.of(0, 20));

            assertThat(result.getContent()).isNotEmpty();
            assertThat(result.getContent())
                    .allMatch(book -> book.getTitle().contains("자바"));
        }

        @Test
        @DisplayName("제목 키워드 '스프링'으로 검색하면 '스프링 인 액션'만 반환된다")
        void titleKeyword_spring_returnsOnlySpring() {
            BookSearchCond cond = new BookSearchCond("스프링", BookSearchField.TITLE);

            Page<Book> result = bookQueryRepository.searchBooks(cond, PageRequest.of(0, 20));

            assertThat(result.getContent())
                    .extracting(Book::getTitle)
                    .containsExactly("스프링 인 액션");
        }

        @Test
        @DisplayName("ISBN으로 검색하면 정확히 일치하는 도서 1건만 반환된다")
        void isbnSearch_returnsExactOne() {
            BookSearchCond cond = new BookSearchCond("9788966262281", BookSearchField.ISBN);

            Page<Book> result = bookQueryRepository.searchBooks(cond, PageRequest.of(0, 20));

            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getIsbn13()).isEqualTo("9788966262281");
        }

        @Test
        @DisplayName("저자로 검색하면 해당 저자의 도서만 반환된다")
        void authorSearch_returnsMatchingAuthor() {
            BookSearchCond cond = new BookSearchCond("김영한", BookSearchField.AUTHOR);

            Page<Book> result = bookQueryRepository.searchBooks(cond, PageRequest.of(0, 20));

            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getTitle()).isEqualTo("자바 ORM 표준 JPA");
        }

        @Test
        @DisplayName("필드 미지정 통합 검색: 제목과 저자 모두 검색된다")
        void noField_searchesAcrossTitleAndAuthor() {
            // "자바"는 제목에 포함된 도서들에서 검색됨
            BookSearchCond cond = new BookSearchCond("자바", null);

            Page<Book> result = bookQueryRepository.searchBooks(cond, PageRequest.of(0, 20));

            assertThat(result.getContent()).hasSizeGreaterThanOrEqualTo(2);
        }

        @Test
        @DisplayName("존재하지 않는 키워드로 검색하면 빈 결과를 반환한다")
        void notExistingKeyword_returnsEmpty() {
            BookSearchCond cond = new BookSearchCond("존재하지않는키워드12345", BookSearchField.TITLE);

            Page<Book> result = bookQueryRepository.searchBooks(cond, PageRequest.of(0, 20));

            assertThat(result.getContent()).isEmpty();
            assertThat(result.getTotalElements()).isZero();
        }
    }

    // ──────────────────────────────────────────────────────────────
    // 페이지네이션
    // ──────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("페이지네이션")
    class PaginationTest {

        @Test
        @DisplayName("size=2 페이지 요청 시 최대 2건만 반환한다")
        void pageSize2_returnsMax2() {
            BookSearchCond cond = new BookSearchCond(null, null);

            Page<Book> result = bookQueryRepository.searchBooks(cond, PageRequest.of(0, 2));

            assertThat(result.getContent()).hasSizeLessThanOrEqualTo(2);
            assertThat(result.getSize()).isEqualTo(2);
        }

        @Test
        @DisplayName("totalElements와 totalPages가 올바르게 계산된다")
        void totalElementsAndPages_areCorrect() {
            BookSearchCond cond = new BookSearchCond(null, null);
            long beforeCount = bookQueryRepository.searchBooks(cond, PageRequest.of(0, 100)).getTotalElements();

            Page<Book> result = bookQueryRepository.searchBooks(cond, PageRequest.of(0, 2));

            assertThat(result.getTotalElements()).isEqualTo(beforeCount);
            assertThat(result.getTotalPages()).isEqualTo((int) Math.ceil((double) beforeCount / 2));
        }
    }

    // ──────────────────────────────────────────────────────────────
    // 가나다 정렬
    // ──────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("가나다(title ASC) 정렬")
    class SortingTest {

        @Test
        @DisplayName("전체 조회 결과가 제목 가나다순으로 정렬된다")
        void searchAll_sortedByTitleAsc() {
            BookSearchCond cond = new BookSearchCond(null, null);

            Page<Book> result = bookQueryRepository.searchBooks(cond, PageRequest.of(0, 20));

            List<String> titles = result.getContent().stream()
                    .map(Book::getTitle).toList();
            List<String> sorted = titles.stream().sorted().toList();

            assertThat(titles).isEqualTo(sorted);
        }
    }

    // ──────────────────────────────────────────────────────────────
    // 재고 수량 포함 서비스 통합
    // ──────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("재고 수량 포함 서비스 통합")
    class StockIntegrationTest {

        @Test
        @DisplayName("searchBooksV1: 각 도서의 재고 수량이 BookSummaryRes에 포함된다")
        void searchBooksV1_includesStockInfo() {
            BookSearchCond cond = new BookSearchCond("이펙티브 자바", BookSearchField.TITLE);

            Page<BookSummaryResponse> result = bookService.searchBooksV1(cond, PageRequest.of(0, 10));

            assertThat(result.getContent()).isNotEmpty();
            BookSummaryResponse summary = result.getContent().get(0);
            // setUp에서 create() 호출 시 BookHold 1건(AVAILABLE) 자동 생성됨
            assertThat(summary.totalStock()).isGreaterThanOrEqualTo(1);
            assertThat(summary.availableStock()).isGreaterThanOrEqualTo(1);
        }

        @Test
        @DisplayName("getBookDetail: 재고 수량이 BookDetailRes에 포함된다")
        void getBookDetail_includesStockInfo() {
            // 이미 setUp에서 등록한 도서를 ISBN으로 찾아 테스트
            Book book = bookRepository.findByIsbn13("9788966262281").orElseThrow();

            BookDetailResponse detail = bookService.getBookDetail(book.getBookId());

            assertThat(detail.bookId()).isEqualTo(book.getBookId());
            assertThat(detail.isbn13()).isEqualTo("9788966262281");
            assertThat(detail.totalStock()).isGreaterThanOrEqualTo(1);
            assertThat(detail.availableStock()).isGreaterThanOrEqualTo(1);
        }

        @Test
        @DisplayName("재고 카운트: countByBookIdAndStatus(AVAILABLE)이 정확하게 반환된다")
        void countByStatus_isAccurate() {
            Book book = bookRepository.findByIsbn13("9791162241769").orElseThrow();

            int total     = bookHoldRepository.countByBookId(book.getBookId());
            int available = bookHoldRepository.countByBookIdAndStatus(
                    book.getBookId(), BookHoldStatus.AVAILABLE);

            assertThat(total).isGreaterThanOrEqualTo(1);
            assertThat(available).isLessThanOrEqualTo(total);
        }
    }

    // ──────────────────────────────────────────────────────────────
    // 헬퍼
    // ──────────────────────────────────────────────────────────────

    private BookCreateRequest sampleReq(String title, String isbn, String author, String publisher) {
        return new BookCreateRequest(
                isbn, null, title, author, publisher,
                LocalDate.now(), null, null, null, null,
                30000, 27000, "정상", 0, null, null, "BOOK", "N"
        );
    }
}
