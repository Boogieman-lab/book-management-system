package com.example.bookmanagementsystembo.book.service;

import com.example.bookmanagementsystembo.book.dto.ExternalBookDto;
import com.example.bookmanagementsystembo.book.infra.BookHoldRepository;
import com.example.bookmanagementsystembo.book.infra.BookRepository;
import com.example.bookmanagementsystembo.book.presentation.dto.BookCreateRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
// 테스트 환경에서만 사용할 API 키를 설정합니다.
//@TestPropertySource(properties = "kakao.api.key=YOUR_ACTUAL_KAKAO_API_KEY")
public class BookIntegrationTest {

    @Autowired
    private BookService bookService;

    @Autowired
    private ExternalBookService externalBookService;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookHoldRepository bookHoldRepository;

    /**
     * 각 테스트 종료 후, 생성된 데이터를 모두 삭제하여 테스트의 독립성을 보장합니다.
     */
    @AfterEach
    void tearDown() {
//        bookHoldRepository.deleteAll();
//        bookRepository.deleteAll();
    }

    @Test
    @DisplayName("외부 API를 통해 받은 신규 도서를 데이터베이스에 저장하고, BookHold도 생성해야 한다.")
    void testCreateNewBookFromExternalApi() {
        // Given: '자바의 정석' 책을 외부 API에서 검색합니다.
        String title = "자바의 정석";
        List<ExternalBookDto> booksFromApi = externalBookService.getBooksByTitle(title);

        // 검색된 책이 하나도 없다면 테스트를 실패시킵니다.
        assertFalse(booksFromApi.isEmpty(), "외부 API에서 검색된 책이 없습니다.");

        // 검색된 첫 번째 책의 ISBN을 가져옵니다.
        String isbn = booksFromApi.get(0).isbn();

        // When: 해당 ISBN의 책이 DB에 없는지 확인합니다.
        bookRepository.findByIsbn(isbn).ifPresent(book -> {
            bookRepository.delete(book);
        });

        // ExternalBookDto를 BookCreateRequest로 변환합니다.
        ExternalBookDto bookDto = booksFromApi.get(0);
        BookCreateRequest request = new BookCreateRequest(
                bookDto.title(),             // title
                bookDto.contents(),          // contents
                bookDto.isbn(),              // isbn
                bookDto.datetime(),         // datetime
                bookDto.authors(),           // authors
                bookDto.translators(),       // translators
                bookDto.publisher(),         // publisher
                bookDto.price(),             // price
                bookDto.salePrice(),         // salePrice
                bookDto.thumbnail(),         // thumbnail
                bookDto.status(),            // status
                bookDto.url()                // url
        );

        // Then: BookService를 통해 도서를 생성합니다.
        Long bookHoldId = bookService.createBook(request);

        // 검증:
        assertNotNull(bookHoldId, "BookHold ID는 null이 아니어야 합니다.");
        assertTrue(bookRepository.findByIsbn(isbn).isPresent(), "새로운 책이 데이터베이스에 저장되어야 합니다.");
        assertTrue(bookHoldRepository.findById(bookHoldId).isPresent(), "새로운 BookHold가 생성되어야 합니다.");
    }

    @Test
    @DisplayName("외부 API를 통해 받은 기존 도서가 이미 DB에 있을 경우, BookHold만 생성해야 한다.")
    void testCreateBookHoldForExistingBookFromExternalApi() {
        // Given
        // 1. 책을 외부 API에서 검색합니다.
        String title = "자바 ORM 표준 JPA 프로그래밍";
        List<ExternalBookDto> booksFromApi = externalBookService.getBooksByTitle(title);
        assertFalse(booksFromApi.isEmpty(), "외부 API에서 검색된 책이 없습니다.");

        // 2. BookCreateRequest로 변환
        // 기존에 데이터베이스에 책을 미리 저장합니다.
        ExternalBookDto bookDto = booksFromApi.get(0);
        BookCreateRequest request = new BookCreateRequest(
                bookDto.title(),             // title
                bookDto.contents(),          // contents
                bookDto.isbn(),              // isbn
                bookDto.datetime(),         // datetime
                bookDto.authors(),           // authors
                bookDto.translators(),       // translators
                bookDto.publisher(),         // publisher
                bookDto.price(),             // price
                bookDto.salePrice(),         // salePrice
                bookDto.thumbnail(),         // thumbnail
                bookDto.status(),            // status
                bookDto.url()                // url
        );
        // 3. 테스트를 위해 DB에 책을 미리 생성 (첫 번째 호출)
        bookService.createBook(request);

        // 4. 초기 책 개수와 BookHold 개수 확인
        long initialBookCount = bookRepository.count();
        long initialBookHoldCount = bookHoldRepository.count();

        // When
        // 5. 동일한 책에 대해 BookService를 다시 호출
        Long newBookHoldId = bookService.createBook(request);

        // Then
        // 6. 결과 검증
        assertNotNull(newBookHoldId, "BookHold ID는 null이 아니어야 합니다.");
        assertEquals(initialBookCount, bookRepository.count(), "기존 책이므로 book 테이블의 레코드 수는 변하지 않아야 합니다.");
        assertEquals(initialBookHoldCount + 1, bookHoldRepository.count(), "BookHold는 하나 더 생성되어야 합니다.");
        assertTrue(bookHoldRepository.findById(newBookHoldId).isPresent(), "새로운 BookHold가 생성되어야 합니다.");
    }
}