package com.example.bookmanagementsystembo.book.service;

import com.example.bookmanagementsystembo.book.dto.BookCreateReq;
import com.example.bookmanagementsystembo.book.dto.BookRes;
import com.example.bookmanagementsystembo.book.entity.Book;
import com.example.bookmanagementsystembo.bookHold.repository.BookHoldRepository;
import com.example.bookmanagementsystembo.book.repository.BookRepository;
import com.example.bookmanagementsystembo.exception.CoreException;
import com.example.bookmanagementsystembo.exception.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


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
    @DisplayName("create: 신규 도서이면 Book 1건 저장되고 BookHold 1건 생성된다")
    void create_newBook_savesBookAndBookHold() {
        // given
        BookCreateReq req = sampleCreateReq("자바의 정석", "9791188612678");
        long beforeBooks = bookRepository.count();
        long beforeHolds = bookHoldRepository.count();

        // when
        BookRes result = bookService.create(req);

        // then
        assertNotNull(result, "create 결과(BookRes)는 null이 아니어야 한다.");
        assertEquals(req.title(), result.title(), "응답의 title은 요청과 동일해야 한다.");
        assertEquals(req.isbn(), result.isbn(), "응답의 isbn은 요청과 동일해야 한다.");

        assertEquals(beforeBooks + 1, bookRepository.count(), "신규 도서이므로 book 1건이 추가되어야 한다.");
        assertEquals(beforeHolds + 1, bookHoldRepository.count(), "BookHold 1건이 생성되어야 한다.");

        Book saved = bookRepository.findByIsbn(req.isbn()).orElseThrow();
        assertEquals(req.title(), saved.getTitle(), "실제 저장된 book의 title이 요청과 같아야 한다.");
        assertEquals(req.isbn(), saved.getIsbn(), "실제 저장된 book의 isbn이 요청과 같아야 한다.");
    }



    @Test
    @DisplayName("create: 이미 존재하는 ISBN이면 Book은 재사용하고 BookHold만 1건 추가된다")
    void create_existingIsbn_reusesBook_andCreatesAnotherBookHold() {
        // given
        String isbn = "9788960773431";
        BookCreateReq req1 = sampleCreateReq("토비의 스프링 1", isbn);
        BookCreateReq req2 = sampleCreateReq("토비의 스프링 2(제목은 달라도 ISBN 같음)", isbn);

        // 최초 1번 생성
        bookService.create(req1);
        long beforeBooks = bookRepository.count();
        long beforeHolds = bookHoldRepository.count();

        // when: 같은 ISBN으로 다시 create 호출
        BookRes result = bookService.create(req2);

        // then
        assertNotNull(result);
        // ISBN 기준으로는 같은 Book을 찾아야 함
        Book saved = bookRepository.findByIsbn(isbn).orElseThrow();
        assertEquals(beforeBooks, bookRepository.count(), "기존 ISBN이면 book 수는 늘어나지 않아야 한다.");
        assertEquals(beforeHolds + 1, bookHoldRepository.count(), "BookHold는 1건 추가되어야 한다.");
        assertEquals(isbn, saved.getIsbn());
    }


    @Test
    @DisplayName("read: 존재하는 bookId면 Book을 BookRes로 매핑해 반환한다")
    void read_existingBook_returnsBookRes() {
        // given: 먼저 create로 한 권 저장
        BookCreateReq req = sampleCreateReq("토비의 스프링", "9788960773431");
        BookRes created = bookService.create(req);
        assertNotNull(created);

        Long bookId = bookRepository.findByIsbn(req.isbn())
                .orElseThrow()
                .getBookId();

        // when
        BookRes dto = bookService.read(bookId);

        // then
        assertNotNull(dto, "read 결과는 null이 아니어야 한다.");
        assertEquals(bookId, dto.bookId(), "bookId가 일치해야 한다.");
        assertEquals(req.title(), dto.title(), "title이 create 요청과 동일해야 한다.");
        assertEquals(req.isbn(), dto.isbn(), "isbn이 create 요청과 동일해야 한다.");
    }



    @Test
    @DisplayName("read: 존재하지 않는 bookId면 BOOK_NOT_FOUND CoreException을 던진다")
    void read_notExistingBook_throwsCoreException() {
        // given
        Long notExistingId = 999999L;

        // when & then
        CoreException ex = assertThrows(CoreException.class,
                () -> bookService.read(notExistingId),
                "존재하지 않는 ID 조회 시 CoreException이 발생해야 한다.");

        assertEquals(ErrorType.BOOK_NOT_FOUND, ex.getErrorType(),
                "예외의 ErrorType은 BOOK_NOT_FOUND여야 한다.");
    }

    private BookCreateReq sampleCreateReq(String title, String isbn) {
        return new BookCreateReq(
                title,                                   // title
                "내용 예시",                               // contents
                "https://example.com/detail",            // url
                isbn,                                    // isbn
                LocalDateTime.now(),                     // 출판일
                List.of("저자A", "저자B"),                 // authors
                List.of("번역가A"),                    // translators
                "출판사예시",                               // publisher
                30000,                                   // price
                27000,                                   // salePrice
                "https://example.com/thumb.jpg"          // thumbnail
        );
    }
}