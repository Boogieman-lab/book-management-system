package com.example.bookmanagementsystembo.book.service;

import com.example.bookmanagementsystembo.book.dto.BookCreateReq;
import com.example.bookmanagementsystembo.book.dto.BookDetailRes;
import com.example.bookmanagementsystembo.book.dto.BookSearchCond;
import com.example.bookmanagementsystembo.book.dto.BookSummaryRes;
import com.example.bookmanagementsystembo.book.dto.BookUpdateReq;
import com.example.bookmanagementsystembo.book.entity.Book;
import com.example.bookmanagementsystembo.bookHold.entity.BookHold;
import com.example.bookmanagementsystembo.book.enums.BookSearchField;
import com.example.bookmanagementsystembo.bookHold.enums.BookHoldStatus;
import com.example.bookmanagementsystembo.bookHold.repository.BookHoldRepository;
import com.example.bookmanagementsystembo.book.repository.BookQueryRepository;
import com.example.bookmanagementsystembo.book.repository.BookRepository;
import com.example.bookmanagementsystembo.book.dto.BookRes;
import com.example.bookmanagementsystembo.exception.CoreException;
import com.example.bookmanagementsystembo.exception.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    public Page<BookSummaryRes> searchBooksV1(BookSearchCond cond, Pageable pageable) {
        return bookQueryRepository.searchBooks(cond, pageable)
                .map(book -> {
                    int total     = bookHoldRepository.countByBookId(book.getBookId());
                    int available = bookHoldRepository.countByBookIdAndStatus(
                            book.getBookId(), BookHoldStatus.AVAILABLE);
                    return BookSummaryRes.of(book, available, total);
                });
    }

    /**
     * 도서 상세 조회 — 전체 메타 정보 + 실재고 수량 반환.
     */
    public BookDetailRes getBookDetail(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new CoreException(ErrorType.BOOK_NOT_FOUND, bookId));
        int total     = bookHoldRepository.countByBookId(bookId);
        int available = bookHoldRepository.countByBookIdAndStatus(bookId, BookHoldStatus.AVAILABLE);
        return BookDetailRes.of(book, available, total);
    }

    // ──────────────────────────────────────────────────────────────
    // 기존 API (GET /api/books)
    // ──────────────────────────────────────────────────────────────

    public BookRes read(Long bookId) {
        Book book =  bookRepository.findById(bookId)
                .orElseThrow(() -> new CoreException(ErrorType.BOOK_NOT_FOUND, bookId));
        return BookRes.from(book);
    }

    @Transactional
    public BookRes create(BookCreateReq request) {

        Book book = bookRepository.findByIsbn(request.isbn())
                .orElseGet(() -> bookRepository.save(Book.create(request)));

        bookHoldRepository.save(BookHold.create(book.getBookId()));

        return BookRes.from(book);
    }

    @Transactional
    public BookRes update(Long bookId, BookUpdateReq request) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new CoreException(ErrorType.BOOK_NOT_FOUND, bookId));

        book.update(request);
        return BookRes.from(book);
    }

    @Transactional
    public void delete(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new CoreException(ErrorType.BOOK_NOT_FOUND, bookId));

        bookRepository.delete(book);
        bookHoldRepository.deleteByBookId(bookId);
    }

    public List<BookRes> searchBooks(BookSearchField field, String query) {
        return switch (field) {
            case AUTHOR -> getBooksByAuthor(query);
            case PUBLISHER -> getBooksByPublisher(query);
            case ISBN -> getBooksByIsbn(query);
            case TITLE -> getBooksByTitle(query);
        };
    }

    private List<BookRes> getBooksByTitle(String title) {
        return bookRepository.findByTitleContaining(title)
                .stream()
                .map(BookRes::from)
                .toList();
    }


    private List<BookRes> getBooksByIsbn(String query) {
        return bookRepository.findByIsbn(query)
                .stream()
                .map(BookRes::from)
                .toList();
    }

    private List<BookRes> getBooksByPublisher(String query) {
        return bookRepository.findByPublisherContaining(query)
                .stream()
                .map(BookRes::from)
                .toList();
    }

    private List<BookRes> getBooksByAuthor(String query) {
        return bookRepository.findByAuthorsContaining(query)
                .stream()
                .map(BookRes::from)
                .toList();
    }
}
