package com.example.bookmanagementsystembo.book.service;

import com.example.bookmanagementsystembo.book.domain.dto.BookCreateDto;
import com.example.bookmanagementsystembo.book.domain.dto.BookDto;
import com.example.bookmanagementsystembo.book.domain.entity.Book;
import com.example.bookmanagementsystembo.book.domain.service.BookService;
import com.example.bookmanagementsystembo.bookHold.infra.BookHoldRepository;
import com.example.bookmanagementsystembo.book.infra.BookRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@SpringBootTest
@Transactional // 각 테스트 메서드가 끝나면 자동 롤백
public class BookServiceIntegrationTest {

    @Autowired
    private BookService bookService;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookHoldRepository bookHoldRepository;


    @Test
    @DisplayName("신규 도서면 book 1건 저장되고, BookHold도 1건 생성된다")
    void createBook_newBook_createsBookAndBookHold() {
        // given
        BookCreateDto req = sampleCreateDto("자바의 정석", "9791188612678");
        long beforeBooks = bookRepository.count();
        long beforeHolds = bookHoldRepository.count();

        // when
        Long bookHoldId = bookService.createBook(req);

        // then
        assertNotNull(bookHoldId, "BookHold ID는 null이 아니어야 합니다.");
        assertEquals(beforeBooks + 1, bookRepository.count(), "신규 도서이므로 book 1건이 추가되어야 합니다.");
        assertEquals(beforeHolds + 1, bookHoldRepository.count(), "BookHold 1건이 생성되어야 합니다.");

        Book saved = bookRepository.findByIsbn(req.isbn()).orElseThrow();
        assertEquals(req.title(), saved.getTitle());
        assertEquals(req.isbn(), saved.getIsbn());
    }

    @Test
    @DisplayName("getBookById는 Book을 BookDto로 매핑해 반환한다")
    void getBookById_returnsBookDto() {
        // given
        BookCreateDto req = sampleCreateDto("토비의 스프링", "9788960773431");
        Long holdId = bookService.createBook(req);
        assertNotNull(holdId);
        Long bookId = bookRepository.findByIsbn(req.isbn()).orElseThrow().getBookId();

        // when
        BookDto dto = bookService.getBookById(bookId);

        // then
        assertNotNull(dto);
        assertEquals(req.title(), dto.title());
        assertEquals(req.isbn(), dto.isbn());
    }


    private BookCreateDto sampleCreateDto(String title, String isbn) {
        // Book.create(BookCreateDto)에 맞춰 필드 세팅
        return new BookCreateDto(
                title,                                   // title
                "내용 예시",                               // contents
                isbn,                                    // isbn
                LocalDateTime.now(),                     // datetime
                List.of("저자A", "저자B"),                 // authors
                List.of("번역가A"),                    // translators
                "출판사예시",                               // publisher
                30000,                                   // price
                27000,                                   // salePrice
                "https://example.com/thumb.jpg",         // thumbnail
                "정상판매",                                // status
                "https://example.com/detail"             // url
        );
    }
}