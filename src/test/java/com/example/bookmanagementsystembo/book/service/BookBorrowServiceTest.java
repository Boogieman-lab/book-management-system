package com.example.bookmanagementsystembo.book.service;

import com.example.bookmanagementsystembo.book.dto.BookBorrowDetailDto;
import com.example.bookmanagementsystembo.book.dto.BookBorrowDto;
import com.example.bookmanagementsystembo.book.infra.BookBorrowRepository;
import com.example.bookmanagementsystembo.book.infra.BookHoldRepository;
import com.example.bookmanagementsystembo.book.infra.BookRepository;
import com.example.bookmanagementsystembo.department.repository.DepartmentRepository;
import com.example.bookmanagementsystembo.user.infra.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class BookBorrowServiceTest {
    @InjectMocks
    private BookBorrowService bookBorrowService;

    @Mock
    private BookBorrowRepository bookBorrowRepository;

    private LocalDateTime fixed;
    private BookBorrowDto bookBorrowDto;
    private BookBorrowDetailDto bookBorrowDetailDto;

    @BeforeEach
    void setUp() {
        fixed = LocalDateTime.of(2025, 9, 16, 10, 0, 0);
        bookBorrowDto = new BookBorrowDto(
                1L,
                "부기맨",
                "홍길동",
                "대출완료",
                fixed
        );

        bookBorrowDetailDto = new BookBorrowDetailDto(
                1L,
                "부기맨",
                "홍길동",
                "대출완료",
                fixed,
                "공부사유",
                "이노베이션"
                );
    }
    @Test
    @DisplayName("도서 대출 목록 조회")
    void findBookBorrows() {
        // Given
        when(bookBorrowRepository.findBookBorrows()).thenReturn(List.of(bookBorrowDto));
        // When
        List<BookBorrowDto> results = bookBorrowService.getBookBorrows();

        // Then
        assertThat(results).hasSize(1);
        assertThat(results.get(0).bookBorrowId()).isEqualTo(1L);
        assertThat(results.get(0).title()).isEqualTo("부기맨");
        assertThat(results.get(0).userName()).isEqualTo("홍길동");
        assertThat(results.get(0).status()).isEqualTo("대출완료");
        assertThat(results.get(0).createdAt()).isEqualTo(fixed);

        verify(bookBorrowRepository, times(1)).findBookBorrows();
    }
    @Test
    @DisplayName("도서 대출 목록 조회")
    void findBookBorrow() {
        // Given
        Long bookBorrowId = 1L;
        when(bookBorrowRepository.findBookBorrow(bookBorrowId)).thenReturn(bookBorrowDetailDto);
        // When
        BookBorrowDetailDto result = bookBorrowService.getBookBorrow(bookBorrowId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.bookBorrowId()).isEqualTo(1L);
        assertThat(result.title()).isEqualTo("부기맨");
        assertThat(result.userName()).isEqualTo("홍길동");
        assertThat(result.status()).isEqualTo("대출완료");
        assertThat(result.createdAt()).isEqualTo(fixed);
        verify(bookBorrowRepository, times(1)).findBookBorrow(bookBorrowId);
    }



}