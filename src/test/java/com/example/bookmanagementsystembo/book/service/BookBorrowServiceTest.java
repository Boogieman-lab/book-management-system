package com.example.bookmanagementsystembo.book.service;

import com.example.bookmanagementsystembo.bookBorrow.domain.dto.BookBorrowDetailDto;
import com.example.bookmanagementsystembo.bookBorrow.domain.dto.BookBorrowDto;
import com.example.bookmanagementsystembo.bookBorrow.domain.entity.BookBorrow;
import com.example.bookmanagementsystembo.bookBorrow.domain.service.BookBorrowService;
import com.example.bookmanagementsystembo.bookBorrow.enums.BorrowStatus;
import com.example.bookmanagementsystembo.bookBorrow.infra.BookBorrowRepository;
import com.example.bookmanagementsystembo.exception.CoreException;
import com.example.bookmanagementsystembo.exception.ErrorType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    @Test
    @DisplayName("상태 업데이트 성공")
    void updateBookBorrow_success() {
        // Given
        Long borrowId = 1L;
        BookBorrow entity = BookBorrow.create(10L, 20L, "사유"); // 기존 상태 BORROWED
        when(bookBorrowRepository.findById(borrowId)).thenReturn(Optional.of(entity));

        // When
        bookBorrowService.updateBookBorrow(borrowId, "RETURNED");

        // Then
        assertEquals(BorrowStatus.RETURNED, entity.getStatus());
        verify(bookBorrowRepository).findById(borrowId);
        verifyNoMoreInteractions(bookBorrowRepository); // dirty checking이므로 save 호출 없음 가정
    }

    @Test
    @DisplayName("잘못된 상태 문자열이면 BORROWSTATUS_NOT_FOUND")
    void updateBookBorrow_fail() {
        // Given
        Long borrowId = 1L;

        // When & Then
        CoreException ex = assertThrows(CoreException.class, () -> bookBorrowService.updateBookBorrow(borrowId, "UNKNOWN_STATUS"));
        assertEquals(ErrorType.BORROWSTATUS_NOT_FOUND, ex.getErrorType());

        // enum 변환에서 이미 예외가 나므로, 레포는 호출되지 않아야 함
        verifyNoInteractions(bookBorrowRepository);
    }

}