package com.example.bookmanagementsystembo.book.service;

import com.example.bookmanagementsystembo.book.dto.BookDetailResponse;
import com.example.bookmanagementsystembo.book.dto.BookSearchCond;
import com.example.bookmanagementsystembo.book.dto.BookSummaryResponse;
import com.example.bookmanagementsystembo.book.entity.Book;
import com.example.bookmanagementsystembo.book.enums.BookSearchField;
import com.example.bookmanagementsystembo.book.repository.BookQueryRepository;
import com.example.bookmanagementsystembo.book.repository.BookRepository;
import com.example.bookmanagementsystembo.bookHold.enums.BookHoldStatus;
import com.example.bookmanagementsystembo.bookHold.repository.BookHoldRepository;
import com.example.bookmanagementsystembo.exception.CoreException;
import com.example.bookmanagementsystembo.exception.ErrorType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@DisplayName("BookService 도서 검색 단위 테스트")
@ExtendWith(MockitoExtension.class)
class BookSearchServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookHoldRepository bookHoldRepository;

    @Mock
    private BookQueryRepository bookQueryRepository;

    @InjectMocks
    private BookService bookService;

    private Book sampleBook;

    @BeforeEach
    void setUp() {
        sampleBook = createBook(1L, "이펙티브 자바", "9788966262281", "[\"조슈아 블로크\"]", "인사이트");
    }

    // ──────────────────────────────────────────────────────────────
    // searchBooksV1 — 목록 검색
    // ──────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("searchBooksV1 - 도서 목록 검색")
    class SearchBooksV1Test {

        @Test
        @DisplayName("키워드 없이 조회하면 전체 도서 목록을 반환한다")
        void noKeyword_returnsAll() {
            Pageable pageable = PageRequest.of(0, 10);
            BookSearchCond cond = new BookSearchCond(null, null);
            Page<Book> bookPage = new PageImpl<>(List.of(sampleBook), pageable, 1);

            when(bookQueryRepository.searchBooks(cond, pageable)).thenReturn(bookPage);
            when(bookHoldRepository.countByBookId(1L)).thenReturn(3);
            when(bookHoldRepository.countByBookIdAndStatus(1L, BookHoldStatus.AVAILABLE)).thenReturn(2);

            Page<BookSummaryResponse> result = bookService.searchBooksV1(cond, pageable);

            assertThat(result.getTotalElements()).isEqualTo(1);
            BookSummaryResponse summary = result.getContent().get(0);
            assertThat(summary.bookId()).isEqualTo(1L);
            assertThat(summary.title()).isEqualTo("이펙티브 자바");
            assertThat(summary.totalStock()).isEqualTo(3);
            assertThat(summary.availableStock()).isEqualTo(2);
        }

        @Test
        @DisplayName("제목 키워드로 검색하면 해당 도서만 반환한다")
        void titleKeyword_returnsMatchingBooks() {
            Pageable pageable = PageRequest.of(0, 10);
            BookSearchCond cond = new BookSearchCond("자바", BookSearchField.TITLE);
            Page<Book> bookPage = new PageImpl<>(List.of(sampleBook), pageable, 1);

            when(bookQueryRepository.searchBooks(cond, pageable)).thenReturn(bookPage);
            when(bookHoldRepository.countByBookId(1L)).thenReturn(2);
            when(bookHoldRepository.countByBookIdAndStatus(1L, BookHoldStatus.AVAILABLE)).thenReturn(1);

            Page<BookSummaryResponse> result = bookService.searchBooksV1(cond, pageable);

            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).title()).contains("자바");
        }

        @Test
        @DisplayName("ISBN으로 검색하면 정확히 일치하는 도서를 반환한다")
        void isbnSearch_returnsExactMatch() {
            Pageable pageable = PageRequest.of(0, 10);
            BookSearchCond cond = new BookSearchCond("9788966262281", BookSearchField.ISBN);
            Page<Book> bookPage = new PageImpl<>(List.of(sampleBook), pageable, 1);

            when(bookQueryRepository.searchBooks(cond, pageable)).thenReturn(bookPage);
            when(bookHoldRepository.countByBookId(1L)).thenReturn(1);
            when(bookHoldRepository.countByBookIdAndStatus(1L, BookHoldStatus.AVAILABLE)).thenReturn(1);

            Page<BookSummaryResponse> result = bookService.searchBooksV1(cond, pageable);

            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).isbn()).isEqualTo("9788966262281");
        }

        @Test
        @DisplayName("검색 결과가 없으면 빈 페이지를 반환한다")
        void noResult_returnsEmptyPage() {
            Pageable pageable = PageRequest.of(0, 10);
            BookSearchCond cond = new BookSearchCond("존재하지않는도서", BookSearchField.TITLE);
            Page<Book> emptyPage = new PageImpl<>(List.of(), pageable, 0);

            when(bookQueryRepository.searchBooks(cond, pageable)).thenReturn(emptyPage);

            Page<BookSummaryResponse> result = bookService.searchBooksV1(cond, pageable);

            assertThat(result.getContent()).isEmpty();
            assertThat(result.getTotalElements()).isZero();
            verify(bookHoldRepository, never()).countByBookId(any());
        }

        @Test
        @DisplayName("여러 도서가 있을 때 각 도서의 재고 수량을 개별 조회한다")
        void multiplebooks_eachHasOwnStock() {
            Book book2 = createBook(2L, "스프링 인 액션", "1234567890", "[\"크레이그 월스\"]", "제이펍");
            Pageable pageable = PageRequest.of(0, 10);
            BookSearchCond cond = new BookSearchCond(null, null);
            Page<Book> bookPage = new PageImpl<>(List.of(sampleBook, book2), pageable, 2);

            when(bookQueryRepository.searchBooks(cond, pageable)).thenReturn(bookPage);
            when(bookHoldRepository.countByBookId(1L)).thenReturn(3);
            when(bookHoldRepository.countByBookIdAndStatus(1L, BookHoldStatus.AVAILABLE)).thenReturn(2);
            when(bookHoldRepository.countByBookId(2L)).thenReturn(5);
            when(bookHoldRepository.countByBookIdAndStatus(2L, BookHoldStatus.AVAILABLE)).thenReturn(4);

            Page<BookSummaryResponse> result = bookService.searchBooksV1(cond, pageable);

            assertThat(result.getContent()).hasSize(2);
            assertThat(result.getContent().get(0).totalStock()).isEqualTo(3);
            assertThat(result.getContent().get(1).totalStock()).isEqualTo(5);
        }
    }

    // ──────────────────────────────────────────────────────────────
    // getBookDetail — 도서 상세 조회
    // ──────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("getBookDetail - 도서 상세 조회")
    class GetBookDetailTest {

        @Test
        @DisplayName("존재하는 bookId로 조회하면 전체 정보와 재고 수량을 반환한다")
        void existingBook_returnsDetailWithStock() {
            when(bookRepository.findById(1L)).thenReturn(Optional.of(sampleBook));
            when(bookHoldRepository.countByBookId(1L)).thenReturn(5);
            when(bookHoldRepository.countByBookIdAndStatus(1L, BookHoldStatus.AVAILABLE)).thenReturn(3);

            BookDetailResponse result = bookService.getBookDetail(1L);

            assertThat(result.bookId()).isEqualTo(1L);
            assertThat(result.title()).isEqualTo("이펙티브 자바");
            assertThat(result.isbn()).isEqualTo("9788966262281");
            assertThat(result.totalStock()).isEqualTo(5);
            assertThat(result.availableStock()).isEqualTo(3);
        }

        @Test
        @DisplayName("대출 가능 재고가 0이어도 전체 재고 수량은 정상 반환된다")
        void allBorrowed_availableStockIsZero() {
            when(bookRepository.findById(1L)).thenReturn(Optional.of(sampleBook));
            when(bookHoldRepository.countByBookId(1L)).thenReturn(2);
            when(bookHoldRepository.countByBookIdAndStatus(1L, BookHoldStatus.AVAILABLE)).thenReturn(0);

            BookDetailResponse result = bookService.getBookDetail(1L);

            assertThat(result.totalStock()).isEqualTo(2);
            assertThat(result.availableStock()).isEqualTo(0);
        }

        @Test
        @DisplayName("존재하지 않는 bookId면 BOOK_NOT_FOUND CoreException을 던진다")
        void notExistingBook_throwsCoreException() {
            when(bookRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> bookService.getBookDetail(999L))
                    .isInstanceOf(CoreException.class)
                    .satisfies(ex ->
                            assertThat(((CoreException) ex).getErrorType())
                                    .isEqualTo(ErrorType.BOOK_NOT_FOUND)
                    );
        }
    }

    // ──────────────────────────────────────────────────────────────
    // 헬퍼
    // ──────────────────────────────────────────────────────────────

    private Book createBook(Long id, String title, String isbn, String authors, String publisher) {
        Book book = new Book(id, title, "내용", "http://url.com", isbn,
                null, authors, null, publisher, 30000, 27000, "http://thumb.com", "정상");
        return book;
    }
}
