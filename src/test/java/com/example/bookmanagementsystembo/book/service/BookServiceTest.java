package com.example.bookmanagementsystembo.book.service;

import com.example.bookmanagementsystembo.book.dto.BookCreateReq;
import com.example.bookmanagementsystembo.book.dto.BookRes;
import com.example.bookmanagementsystembo.book.dto.BookUpdateReq;
import com.example.bookmanagementsystembo.book.entity.Book;
import com.example.bookmanagementsystembo.bookHold.entity.BookHold;
import com.example.bookmanagementsystembo.bookHold.repository.BookHoldRepository;
import com.example.bookmanagementsystembo.book.repository.BookRepository;
import com.example.bookmanagementsystembo.exception.CoreException;
import com.example.bookmanagementsystembo.exception.ErrorType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import java.util.List;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @InjectMocks
    private BookService bookService;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookHoldRepository bookHoldRepository;


    private BookCreateReq bookCreateReq;
    private BookUpdateReq bookUpdateReq;

    private Book book;
    private BookHold bookHold;

    @BeforeEach
    void setUp() {
        bookCreateReq = new BookCreateReq(
                "이펙티브 자바",
                "소프트웨어",
                "http//example.com/url",
                "9788966261549",
                LocalDateTime.of(2025, 9, 24, 10, 0, 0),
                List.of("조슈아 블로크"),
                List.of("김중완"),
                "인사이트",
                10000,
                8000,
                "http://example.com/thumbnail"
        );

        bookUpdateReq = new BookUpdateReq(
                "이펙티브 자바 3판",
                "소프트웨어 엔지니어링",
                "http//example.com/url/new",
                List.of("조슈아 블로크", "김수현"),
                List.of("김중완", "이승원"),
                "인사이트",
                12000,
                10000,
                "http://example.com/thumbnail/new"
        );

        book = Book.create(bookCreateReq);
        bookHold = BookHold.create(1L);
    }

    @Test
    @DisplayName("책 ID로 조회 성공")
    void getBookById_success() {
        // Given
        Long bookId = 1L;
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        // When
        BookRes result = bookService.read(bookId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.bookId()).isEqualTo(book.getBookId());
        assertThat(result.title()).isEqualTo(book.getTitle());
        verify(bookRepository, times(1)).findById(bookId);
    }

    @Test
    @DisplayName("책 ID로 조회 실패 - 책을 찾을 수 없음")
    void getBookById_fail() {
        // Given
        Long bookId = 99L;
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        // When & Then
        CoreException ex = assertThrows(CoreException.class, () -> bookService.read(bookId));
        assertEquals(ErrorType.BOOK_NOT_FOUND, ex.getErrorType());
        verify(bookRepository, times(1)).findById(bookId);
    }

    @Test
    @DisplayName("책 생성 성공 - 신규 도서")
    void createBook_success_new() {
        // Given
        when(bookRepository.findByIsbn(bookCreateReq.isbn())).thenReturn(Optional.empty());
        when(bookRepository.save(any(Book.class))).thenReturn(book);
        when(bookHoldRepository.save(any(BookHold.class))).thenReturn(bookHold);

        // When
        BookRes result = bookService.create(bookCreateReq);

        // Then
        verify(bookRepository, times(1)).findByIsbn(bookCreateReq.isbn());
        verify(bookRepository, times(1)).save(any(Book.class));
        verify(bookHoldRepository, times(1)).save(any(BookHold.class));
    }

    @Test
    @DisplayName("책 생성 성공 - 기존 도서")
    void createBook_success_existing() {
        // Given
        when(bookRepository.findByIsbn(bookCreateReq.isbn())).thenReturn(Optional.of(book));
        when(bookHoldRepository.save(any(BookHold.class))).thenReturn(bookHold);

        // When
        BookRes result = bookService.create(bookCreateReq);

        // Then
        verify(bookRepository, times(1)).findByIsbn(bookCreateReq.isbn());
        verify(bookRepository, times(0)).save(any(Book.class));
        verify(bookHoldRepository, times(1)).save(any(BookHold.class));
    }

    @Test
    @DisplayName("책 정보 업데이트 성공")
    void updateBook_success() {
        // Given
        Long bookId = 1L;
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        // When
        bookService.update(bookId, bookUpdateReq);

        // Then
        assertThat(book.getTitle()).isEqualTo(bookUpdateReq.title());
        assertThat(book.getContents()).isEqualTo(bookUpdateReq.contents());
        verify(bookRepository, times(1)).findById(bookId);
        verifyNoMoreInteractions(bookRepository);
    }

    @Test
    @DisplayName("책 삭제 성공")
    void deleteBook_success() {
        // Given
        Long bookId = 1L;
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        // When
        bookService.delete(bookId);

        // Then
        verify(bookRepository, times(1)).findById(bookId);
        verify(bookRepository, times(1)).delete(book);
        verify(bookHoldRepository, times(1)).deleteByBookId(bookId);
    }

    @Test
    @DisplayName("책 삭제 실패 - 책을 찾을 수 없음")
    void deleteBook_fail() {
        // Given
        Long bookId = 99L;
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        // When & Then
        CoreException ex = assertThrows(CoreException.class, () -> bookService.delete(bookId));
        assertEquals(ErrorType.BOOK_NOT_FOUND, ex.getErrorType());
        verify(bookRepository, times(1)).findById(bookId);
        verify(bookRepository, times(0)).deleteByBookId(any());
        verify(bookHoldRepository, times(0)).deleteByBookId(any());
    }
}