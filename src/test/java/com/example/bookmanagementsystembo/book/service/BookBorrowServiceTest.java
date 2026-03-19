package com.example.bookmanagementsystembo.book.service;

import com.example.bookmanagementsystembo.bookBorrow.dto.BookBorrowDetailDto;
import com.example.bookmanagementsystembo.bookBorrow.dto.BookBorrowDto;
import com.example.bookmanagementsystembo.bookBorrow.dto.BorrowRes;
import com.example.bookmanagementsystembo.bookBorrow.entity.BookBorrow;
import com.example.bookmanagementsystembo.bookBorrow.service.BookBorrowService;
import com.example.bookmanagementsystembo.bookBorrow.enums.BorrowStatus;
import com.example.bookmanagementsystembo.bookBorrow.repository.BookBorrowRepository;
import com.example.bookmanagementsystembo.bookHold.entity.BookHold;
import com.example.bookmanagementsystembo.bookHold.enums.BookHoldStatus;
import com.example.bookmanagementsystembo.bookHold.repository.BookHoldRepository;
import com.example.bookmanagementsystembo.exception.CoreException;
import com.example.bookmanagementsystembo.exception.ErrorType;
import com.example.bookmanagementsystembo.reservation.domain.entity.Reservation;
import com.example.bookmanagementsystembo.reservation.enums.ReservationStatus;
import com.example.bookmanagementsystembo.reservation.infra.ReservationRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class BookBorrowServiceTest {
    @InjectMocks
    private BookBorrowService bookBorrowService;

    @Mock
    private BookBorrowRepository bookBorrowRepository;

    @Mock
    private BookHoldRepository bookHoldRepository;

    @Mock
    private ReservationRepository reservationRepository;

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
        List<BookBorrowDto> results = bookBorrowService.readAll();

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
    @DisplayName("도서 대출 상세 조회")
    void findBookBorrow() {
        // Given
        Long bookBorrowId = 1L;
        when(bookBorrowRepository.findBookBorrow(bookBorrowId)).thenReturn(bookBorrowDetailDto);
        // When
        BookBorrowDetailDto result = bookBorrowService.read(bookBorrowId);

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
        BookBorrow entity = BookBorrow.create(10L, 20L, "사유");
        when(bookBorrowRepository.findById(borrowId)).thenReturn(Optional.of(entity));

        // When
        bookBorrowService.updateBookBorrow(borrowId, "RETURNED");

        // Then
        assertEquals(BorrowStatus.RETURNED, entity.getStatus());
        verify(bookBorrowRepository).findById(borrowId);
        verifyNoMoreInteractions(bookBorrowRepository);
    }

    @Test
    @DisplayName("잘못된 상태 문자열이면 BORROWSTATUS_NOT_FOUND")
    void updateBookBorrow_fail() {
        // Given
        Long borrowId = 1L;

        // When & Then
        CoreException ex = assertThrows(CoreException.class, () -> bookBorrowService.updateBookBorrow(borrowId, "UNKNOWN_STATUS"));
        assertEquals(ErrorType.BORROWSTATUS_NOT_FOUND, ex.getErrorType());

        verifyNoInteractions(bookBorrowRepository);
    }

    // ========== 대출 API 테스트 ==========

    @Test
    @DisplayName("대출 성공")
    void borrow_success() {
        // Given
        Long bookHoldId = 1L;
        Long userId = 100L;
        BookHold bookHold = BookHold.create(10L);
        when(bookHoldRepository.findByIdForUpdate(bookHoldId)).thenReturn(Optional.of(bookHold));
        when(bookBorrowRepository.countByUserIdAndStatus(userId, BorrowStatus.BORROWED)).thenReturn(0);
        when(bookBorrowRepository.save(any(BookBorrow.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        BorrowRes res = bookBorrowService.borrow(bookHoldId, userId, "학습용");

        // Then
        assertThat(res).isNotNull();
        assertThat(res.dueDate()).isNotNull();
        assertThat(bookHold.getStatus()).isEqualTo(BookHoldStatus.BORROWED);
        verify(bookHoldRepository).findByIdForUpdate(bookHoldId);
        verify(bookBorrowRepository).save(any(BookBorrow.class));
    }

    @Test
    @DisplayName("대출 실패 - AVAILABLE 아닌 경우")
    void borrow_fail_notAvailable() {
        // Given
        Long bookHoldId = 1L;
        Long userId = 100L;
        BookHold bookHold = BookHold.create(10L);
        bookHold.updateStatus(BookHoldStatus.BORROWED);
        when(bookHoldRepository.findByIdForUpdate(bookHoldId)).thenReturn(Optional.of(bookHold));

        // When & Then
        CoreException ex = assertThrows(CoreException.class, () -> bookBorrowService.borrow(bookHoldId, userId, "학습용"));
        assertEquals(ErrorType.BOOK_NOT_AVAILABLE, ex.getErrorType());
    }

    @Test
    @DisplayName("대출 실패 - 10권 초과")
    void borrow_fail_limitExceeded() {
        // Given
        Long bookHoldId = 1L;
        Long userId = 100L;
        BookHold bookHold = BookHold.create(10L);
        when(bookHoldRepository.findByIdForUpdate(bookHoldId)).thenReturn(Optional.of(bookHold));
        when(bookBorrowRepository.countByUserIdAndStatus(userId, BorrowStatus.BORROWED)).thenReturn(10);

        // When & Then
        CoreException ex = assertThrows(CoreException.class, () -> bookBorrowService.borrow(bookHoldId, userId, "학습용"));
        assertEquals(ErrorType.BORROW_LIMIT_EXCEEDED, ex.getErrorType());
    }

    // ========== 반납 API 테스트 ==========

    @Test
    @DisplayName("반납 성공 - 예약 없음")
    void returnBook_success_noReservation() {
        // Given
        Long borrowId = 1L;
        Long userId = 100L;
        Long bookHoldId = 10L;
        BookBorrow bookBorrow = BookBorrow.create(bookHoldId, userId, "학습용");
        BookHold bookHold = BookHold.create(5L);
        bookHold.updateStatus(BookHoldStatus.BORROWED);

        when(bookBorrowRepository.findById(borrowId)).thenReturn(Optional.of(bookBorrow));
        when(reservationRepository.countByBookHold_BookHoldIdAndStatus(bookHoldId, ReservationStatus.WAITING)).thenReturn(0);
        when(bookHoldRepository.findById(bookHoldId)).thenReturn(Optional.of(bookHold));

        // When
        bookBorrowService.returnBook(borrowId, userId);

        // Then
        assertEquals(BorrowStatus.RETURNED, bookBorrow.getStatus());
        assertThat(bookBorrow.getReturnDate()).isNotNull();
        assertEquals(BookHoldStatus.AVAILABLE, bookHold.getStatus());
    }

    @Test
    @DisplayName("반납 성공 - 예약 있음 (RESERVE_HOLD)")
    void returnBook_success_withReservation() {
        // Given
        Long borrowId = 1L;
        Long userId = 100L;
        Long bookHoldId = 10L;
        BookBorrow bookBorrow = BookBorrow.create(bookHoldId, userId, "학습용");
        BookHold bookHold = BookHold.create(5L);
        bookHold.updateStatus(BookHoldStatus.BORROWED);
        Reservation reservation = Reservation.create(bookHold, 200L, LocalDateTime.now().plusDays(3));

        when(bookBorrowRepository.findById(borrowId)).thenReturn(Optional.of(bookBorrow));
        when(reservationRepository.countByBookHold_BookHoldIdAndStatus(bookHoldId, ReservationStatus.WAITING)).thenReturn(1);
        when(bookHoldRepository.findById(bookHoldId)).thenReturn(Optional.of(bookHold));
        when(reservationRepository.findByBookHold_BookHoldIdAndStatusOrderByCreatedAtAsc(bookHoldId, ReservationStatus.WAITING))
                .thenReturn(List.of(reservation));

        // When
        bookBorrowService.returnBook(borrowId, userId);

        // Then
        assertEquals(BorrowStatus.RETURNED, bookBorrow.getStatus());
        assertEquals(BookHoldStatus.RESERVE_HOLD, bookHold.getStatus());
    }

    @Test
    @DisplayName("반납 실패 - IDOR (다른 사용자)")
    void returnBook_fail_notOwner() {
        // Given
        Long borrowId = 1L;
        Long ownerUserId = 100L;
        Long attackerUserId = 999L;
        BookBorrow bookBorrow = BookBorrow.create(10L, ownerUserId, "학습용");

        when(bookBorrowRepository.findById(borrowId)).thenReturn(Optional.of(bookBorrow));

        // When & Then
        CoreException ex = assertThrows(CoreException.class, () -> bookBorrowService.returnBook(borrowId, attackerUserId));
        assertEquals(ErrorType.BORROW_NOT_OWNER, ex.getErrorType());
    }

    @Test
    @DisplayName("반납 실패 - 이미 반납됨")
    void returnBook_fail_alreadyReturned() {
        // Given
        Long borrowId = 1L;
        Long userId = 100L;
        BookBorrow bookBorrow = BookBorrow.create(10L, userId, "학습용");
        bookBorrow.returnBook(); // 이미 반납 처리

        when(bookBorrowRepository.findById(borrowId)).thenReturn(Optional.of(bookBorrow));

        // When & Then
        CoreException ex = assertThrows(CoreException.class, () -> bookBorrowService.returnBook(borrowId, userId));
        assertEquals(ErrorType.BORROW_ALREADY_RETURNED, ex.getErrorType());
    }
}
