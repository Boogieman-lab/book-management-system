package com.example.bookmanagementsystembo.book.domain.service;

import com.example.bookmanagementsystembo.book.domain.dto.BookCreateDto;
import com.example.bookmanagementsystembo.book.domain.dto.BookDto;
import com.example.bookmanagementsystembo.book.domain.dto.BookUpdateDto;
import com.example.bookmanagementsystembo.book.domain.entity.Book;
import com.example.bookmanagementsystembo.book.enums.BookSearchField;
import com.example.bookmanagementsystembo.book.infra.BookRepository;
import com.example.bookmanagementsystembo.book.presentation.dto.BookDetailResponse;
import com.example.bookmanagementsystembo.book.presentation.dto.BookResponse;
import com.example.bookmanagementsystembo.bookBorrow.domain.entity.BookBorrow;
import com.example.bookmanagementsystembo.bookBorrow.infra.BookBorrowRepository;
import com.example.bookmanagementsystembo.bookHold.domain.entity.BookHold;
import com.example.bookmanagementsystembo.bookHold.infra.BookHoldRepository;
import com.example.bookmanagementsystembo.exception.CoreException;
import com.example.bookmanagementsystembo.exception.ErrorType;
import com.example.bookmanagementsystembo.reservation.domain.entity.Reservation;
import com.example.bookmanagementsystembo.reservation.infra.ReservationRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class BookService {

    private final BookRepository bookRepository;
    private final BookHoldRepository bookHoldRepository;
    private final BookBorrowRepository bookBorrowRepository;
    private final ReservationRepository reservationRepository;

    public BookDto getBookById(Long bookId) {
        Book book =  bookRepository.findById(bookId)
                .orElseThrow(() -> new CoreException(ErrorType.BOOK_NOT_FOUND, bookId));
        return BookDto.from(book);
    }

    @Transactional
    public Long createBook(BookCreateDto request) {

        Book book = bookRepository.findByIsbn(request.isbn())
                .orElseGet(() -> bookRepository.save(Book.create(request)));


        BookHold bookHold = BookHold.create(book.getBookId());
        BookHold savedBookHold = bookHoldRepository.save(bookHold);

        return savedBookHold.getBookHoldId();
    }

    @Transactional
    public void updateBook(BookUpdateDto request) {
        Book book = bookRepository.findById(request.bookId())
                .orElseThrow(() -> new CoreException(ErrorType.BOOK_NOT_FOUND, request.bookId()));

        book.update(request);
    }

    @Transactional
    public void deleteBook(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new CoreException(ErrorType.BOOK_NOT_FOUND, bookId));

        bookRepository.delete(book);
        bookHoldRepository.deleteByBookId(bookId);
    }

    public List<BookDto> getBooksByTitle(String title) {
        return bookRepository.findByTitleContaining(title)
                .stream()
                .map(BookDto::from)
                .toList();
    }

    // Service
    public List<BookResponse> searchBooks(BookSearchField field, String query) {
        List<BookDto> books = switch (field) {
            case AUTHOR -> getBooksByAuthor(query);
            case PUBLISHER -> getBooksByPublisher(query);
            case ISBN -> getBooksByIsbn(query);
            case TITLE -> getBooksByTitle(query);
        };
        return BookResponse.from(books);
    }

    private List<BookDto> getBooksByIsbn(String query) {
        return bookRepository.findByIsbn(query)
                .stream()
                .map(BookDto::from)
                .toList();
    }

    private List<BookDto> getBooksByPublisher(String query) {
        return bookRepository.findByPublisherContaining(query)
                .stream()
                .map(BookDto::from)
                .toList();
    }

    private List<BookDto> getBooksByAuthor(String query) {
        return bookRepository.findByAuthorsContaining(query)
                .stream()
                .map(BookDto::from)
                .toList();
    }

    // 클래스 멤버로 ObjectMapper 하나 생성
    private final ObjectMapper objectMapper = new ObjectMapper();

    public BookDetailResponse getBookDetail(Long bookId) {

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("도서를 찾을 수 없습니다."));

        // authors JSON 문자열 파싱
        List<String> authorList;
        try {
            authorList = objectMapper.readValue(book.getAuthors(), new TypeReference<List<String>>() {});
        } catch (Exception e) {
            authorList = List.of(); // 예외 시 빈 리스트
        }

        String authorsString = String.join(", ", authorList); // "김기수" or "김기수1, 김기수2"

        // 보유 목록
        List<BookHold> holds = bookHoldRepository.findByBookId(bookId);

        List<BookDetailResponse.HoldInfo> holdInfos = holds.stream()
                .map(hold -> {
                    // 해당 Hold의 최신 대출 정보
                    BookBorrow borrow = bookBorrowRepository
                            .findTopByBookHoldIdOrderByCreatedAtDesc(hold.getBookHoldId());

                    // 상태가 REQUESTED인 예약자만 가져오기
                    List<Reservation> reservations = reservationRepository
                            .findByBookHold_BookHoldIdAndStatusOrderByCreatedAtAsc(hold.getBookHoldId(), "REQUESTED");

                    return BookDetailResponse.HoldInfo.from(hold, borrow, reservations);
                })
                .toList();

        return BookDetailResponse.from(book, authorsString, holdInfos);
    }


}
