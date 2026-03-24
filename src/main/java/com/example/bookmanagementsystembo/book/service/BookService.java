package com.example.bookmanagementsystembo.book.service;

import com.example.bookmanagementsystembo.book.dto.BookCreateRequest;
import com.example.bookmanagementsystembo.book.dto.BookDetailResponse;
import com.example.bookmanagementsystembo.book.dto.BookHoldCountDto;
import com.example.bookmanagementsystembo.book.dto.BookIsbnCheckResponse;
import com.example.bookmanagementsystembo.book.dto.BookSearchCond;
import com.example.bookmanagementsystembo.book.dto.BookSummaryResponse;
import com.example.bookmanagementsystembo.book.dto.BookUpdateRequest;
import com.example.bookmanagementsystembo.book.entity.Book;
import com.example.bookmanagementsystembo.bookHold.entity.BookHold;
import com.example.bookmanagementsystembo.book.enums.BookSearchField;
import com.example.bookmanagementsystembo.bookHold.enums.BookHoldStatus;
import com.example.bookmanagementsystembo.bookHold.repository.BookHoldRepository;
import com.example.bookmanagementsystembo.book.repository.BookQueryRepository;
import com.example.bookmanagementsystembo.book.repository.BookRepository;
import com.example.bookmanagementsystembo.book.dto.BookResponse;
import com.example.bookmanagementsystembo.exception.CoreException;
import com.example.bookmanagementsystembo.exception.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class BookService {

    private final BookRepository bookRepository;
    private final BookHoldRepository bookHoldRepository;
    private final BookQueryRepository bookQueryRepository;

    // ──────────────────────────────────────────────────────────────
    // V1 API (GET /api/v1/books)
    // ──────────────────────────────────────────────────────────────

    /**
     * 도서 목록 조회 — QueryDSL 동적 검색 + 페이지네이션 + 가나다 정렬.
     * 각 도서의 전체 재고 수량과 대출 가능 수량을 함께 반환합니다.
     */
    public Page<BookSummaryResponse> searchBooksV1(BookSearchCond cond, Pageable pageable) {
        Page<Book> books = bookQueryRepository.searchBooks(cond, pageable);

        List<Long> bookIds = books.stream().map(Book::getBookId).toList();
        Map<Long, BookHoldCountDto> holdCounts = bookQueryRepository.countHoldsByBookIds(bookIds);

        return books.map(book -> {
            BookHoldCountDto counts = holdCounts.getOrDefault(book.getBookId(), BookHoldCountDto.EMPTY);
            return BookSummaryResponse.of(book, (int) counts.available(), (int) counts.total());
        });
    }

    /**
     * ISBN13으로 보유 여부 및 총 실물 수량을 조회합니다.
     * 희망 도서 신청 시 기보유 여부 사전 안내에 사용됩니다.
     */
    public BookIsbnCheckResponse checkIsbn(String isbn) {
        return bookRepository.findByIsbn13(isbn)
                .map(book -> {
                    int total = bookHoldRepository.countByBookId(book.getBookId());
                    return new BookIsbnCheckResponse(true, total);
                })
                .orElse(new BookIsbnCheckResponse(false, 0));
    }

    /** 도서 상세 조회 — 전체 메타 정보 + 가용/총 재고 수량을 반환합니다. */
    public BookDetailResponse getBookDetail(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new CoreException(ErrorType.BOOK_NOT_FOUND, bookId));
        int total     = bookHoldRepository.countByBookId(bookId);
        int available = bookHoldRepository.countByBookIdAndStatus(bookId, BookHoldStatus.AVAILABLE);
        return BookDetailResponse.of(book, available, total);
    }

    // ──────────────────────────────────────────────────────────────
    // 기존 API (GET /api/books)
    // ──────────────────────────────────────────────────────────────

    /** bookId로 도서 단건을 조회합니다 (레거시 API). */
    public BookResponse read(Long bookId) {
        Book book =  bookRepository.findById(bookId)
                .orElseThrow(() -> new CoreException(ErrorType.BOOK_NOT_FOUND, bookId));
        return BookResponse.from(book);
    }

    /**
     * 도서를 등록하고 BookHold를 1개 추가합니다.
     * ISBN13 중복 시 기존 도서에 BookHold만 추가합니다.
     */
    @Transactional
    public BookResponse create(BookCreateRequest request) {

        Book book = bookRepository.findByIsbn13(request.isbn13())
                .orElseGet(() -> bookRepository.save(Book.create(request)));

        bookHoldRepository.save(BookHold.create(book.getBookId()));

        return BookResponse.from(book);
    }

    /** 도서 메타 정보(제목, 저자, 출판사, 설명 등)를 수정합니다. */
    @Transactional
    public BookResponse update(Long bookId, BookUpdateRequest request) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new CoreException(ErrorType.BOOK_NOT_FOUND, bookId));

        book.update(request);
        return BookResponse.from(book);
    }

    /** 도서를 삭제하고 연관된 모든 BookHold를 함께 삭제합니다. */
    @Transactional
    public void delete(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new CoreException(ErrorType.BOOK_NOT_FOUND, bookId));

        bookRepository.delete(book);
        bookHoldRepository.deleteByBookId(bookId);
    }

    /** 검색 필드(TITLE/AUTHOR/ISBN/PUBLISHER)와 검색어로 도서 목록을 반환합니다 (레거시 API). */
    public List<BookResponse> searchBooks(BookSearchField field, String query) {
        return switch (field) {
            case AUTHOR -> getBooksByAuthor(query);
            case PUBLISHER -> getBooksByPublisher(query);
            case ISBN -> getBooksByIsbn(query);
            case TITLE -> getBooksByTitle(query);
        };
    }

    private List<BookResponse> getBooksByTitle(String title) {
        return bookRepository.findByTitleContaining(title)
                .stream()
                .map(BookResponse::from)
                .toList();
    }


    private List<BookResponse> getBooksByIsbn(String query) {
        return bookRepository.findByIsbn13(query)
                .stream()
                .map(BookResponse::from)
                .toList();
    }

    private List<BookResponse> getBooksByPublisher(String query) {
        return bookRepository.findByPublisherContaining(query)
                .stream()
                .map(BookResponse::from)
                .toList();
    }

    private List<BookResponse> getBooksByAuthor(String query) {
        return bookRepository.findByAuthorContaining(query)
                .stream()
                .map(BookResponse::from)
                .toList();
    }
}
