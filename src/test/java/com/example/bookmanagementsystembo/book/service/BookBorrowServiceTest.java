package com.example.bookmanagementsystembo.book.service;

import com.example.bookmanagementsystembo.bookBorrow.dto.BookBorrowDetailDto;
import com.example.bookmanagementsystembo.bookBorrow.dto.BookBorrowDto;
import com.example.bookmanagementsystembo.bookBorrow.dto.BorrowResponse;
import com.example.bookmanagementsystembo.bookBorrow.entity.BookBorrow;
import com.example.bookmanagementsystembo.bookBorrow.enums.BorrowStatus;
import com.example.bookmanagementsystembo.bookBorrow.repository.BookBorrowRepository;
import com.example.bookmanagementsystembo.bookBorrow.service.BookBorrowService;
import com.example.bookmanagementsystembo.bookHold.entity.BookHold;
import com.example.bookmanagementsystembo.bookHold.enums.BookHoldStatus;
import com.example.bookmanagementsystembo.bookHold.repository.BookHoldRepository;
import com.example.bookmanagementsystembo.exception.CoreException;
import com.example.bookmanagementsystembo.exception.ErrorType;
import com.example.bookmanagementsystembo.notification.service.NotificationService;
import com.example.bookmanagementsystembo.reservation.domain.entity.Reservation;
import com.example.bookmanagementsystembo.reservation.enums.ReservationStatus;
import com.example.bookmanagementsystembo.reservation.infra.ReservationRepository;
import com.example.bookmanagementsystembo.user.entity.Users;
import com.example.bookmanagementsystembo.user.repository.UserRepository;
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

    @Mock
    private NotificationService notificationService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Users mockUser;

    private static final Long BOOK_ID      = 5L;
    private static final Long BOOK_HOLD_ID = 10L;
    private static final Long USER_ID      = 100L;
    private static final Long BORROW_ID    = 1L;

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

    // ======================================================================
    // 조회
    // ======================================================================

    @Test
    @DisplayName("도서 대출 목록 전체 조회 성공")
    void readAll_success() {
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
    @DisplayName("도서 대출 상세 조회 성공")
    void read_success() {
        // Given
        when(bookBorrowRepository.findBookBorrow(BORROW_ID)).thenReturn(bookBorrowDetailDto);

        // When
        BookBorrowDetailDto result = bookBorrowService.read(BORROW_ID);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.bookBorrowId()).isEqualTo(1L);
        assertThat(result.title()).isEqualTo("부기맨");
        assertThat(result.userName()).isEqualTo("홍길동");
        assertThat(result.status()).isEqualTo("대출완료");
        assertThat(result.createdAt()).isEqualTo(fixed);
        verify(bookBorrowRepository, times(1)).findBookBorrow(BORROW_ID);
    }

    // ======================================================================
    // 상태 업데이트
    // ======================================================================

    @Test
    @DisplayName("대출 상태 업데이트 성공")
    void updateBookBorrow_success() {
        // Given
        BookBorrow bookBorrow = BookBorrow.create(BOOK_HOLD_ID, BOOK_ID, USER_ID, "사유");
        when(bookBorrowRepository.findById(BORROW_ID)).thenReturn(Optional.of(bookBorrow));

        // When
        bookBorrowService.updateBookBorrow(BORROW_ID, "RETURNED");

        // Then
        assertEquals(BorrowStatus.RETURNED, bookBorrow.getStatus());
        verify(bookBorrowRepository).findById(BORROW_ID);
        verifyNoMoreInteractions(bookBorrowRepository);
    }

    @Test
    @DisplayName("대출 상태 업데이트 실패 - 존재하지 않는 borrowId")
    void updateBookBorrow_fail_notFound() {
        // Given
        when(bookBorrowRepository.findById(BORROW_ID)).thenReturn(Optional.empty());

        // When & Then
        CoreException ex = assertThrows(CoreException.class,
                () -> bookBorrowService.updateBookBorrow(BORROW_ID, "RETURNED"));
        assertEquals(ErrorType.BOOKBORROW_NOT_FOUND, ex.getErrorType());
    }

    @Test
    @DisplayName("대출 상태 업데이트 실패 - 잘못된 상태 문자열")
    void updateBookBorrow_fail_invalidStatus() {
        // Given
        // (상태 파싱은 findById 호출 전에 실패하므로 repository mock 불필요)

        // When & Then
        CoreException ex = assertThrows(CoreException.class,
                () -> bookBorrowService.updateBookBorrow(BORROW_ID, "UNKNOWN_STATUS"));
        assertEquals(ErrorType.BORROWSTATUS_NOT_FOUND, ex.getErrorType());
        verifyNoInteractions(bookBorrowRepository);
    }

    // ======================================================================
    // createBookBorrow (레거시 API 경로)
    // ======================================================================

    @Test
    @DisplayName("createBookBorrow 성공 - bookHold에서 bookId 조회 후 저장")
    void createBookBorrow_success() {
        // Given
        BookHold bookHold = BookHold.create(BOOK_ID);
        BookBorrow saved = BookBorrow.create(BOOK_HOLD_ID, BOOK_ID, USER_ID, "사유");
        when(bookHoldRepository.findById(BOOK_HOLD_ID)).thenReturn(Optional.of(bookHold));
        when(bookBorrowRepository.save(any(BookBorrow.class))).thenReturn(saved);

        // When
        bookBorrowService.createBookBorrow(BOOK_HOLD_ID, USER_ID, "사유");

        // Then
        verify(bookHoldRepository).findById(BOOK_HOLD_ID);
        verify(bookBorrowRepository).save(any(BookBorrow.class));
    }

    @Test
    @DisplayName("createBookBorrow 실패 - 존재하지 않는 bookHoldId")
    void createBookBorrow_fail_holdNotFound() {
        // Given
        when(bookHoldRepository.findById(BOOK_HOLD_ID)).thenReturn(Optional.empty());

        // When & Then
        CoreException ex = assertThrows(CoreException.class,
                () -> bookBorrowService.createBookBorrow(BOOK_HOLD_ID, USER_ID, "사유"));
        assertEquals(ErrorType.BOOK_HOLD_NOT_FOUND, ex.getErrorType());
        verifyNoInteractions(bookBorrowRepository);
    }

    // ======================================================================
    // borrow (메인 대출 API)
    // ======================================================================

    @Test
    @DisplayName("대출 성공 - bookId가 bookBorrow에 정상 설정됨")
    void borrow_success() {
        // Given
        BookHold bookHold = BookHold.create(BOOK_ID);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(mockUser));
        when(bookHoldRepository.findByIdForUpdate(BOOK_HOLD_ID)).thenReturn(Optional.of(bookHold));
        when(bookBorrowRepository.countByUserIdAndStatus(USER_ID, BorrowStatus.BORROWED)).thenReturn(0);
        when(bookBorrowRepository.save(any(BookBorrow.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        BorrowResponse response = bookBorrowService.borrow(BOOK_HOLD_ID, USER_ID, "학습용");

        // Then
        assertThat(response).isNotNull();
        assertThat(response.dueDate()).isNotNull();
        assertEquals(BookHoldStatus.BORROWED, bookHold.getStatus());
        verify(bookHoldRepository).findByIdForUpdate(BOOK_HOLD_ID);
        verify(bookBorrowRepository).save(any(BookBorrow.class));
    }

    @Test
    @DisplayName("대출 실패 - 존재하지 않는 bookHoldId")
    void borrow_fail_holdNotFound() {
        // Given
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(mockUser));
        when(bookHoldRepository.findByIdForUpdate(BOOK_HOLD_ID)).thenReturn(Optional.empty());

        // When & Then
        CoreException ex = assertThrows(CoreException.class,
                () -> bookBorrowService.borrow(BOOK_HOLD_ID, USER_ID, "학습용"));
        assertEquals(ErrorType.BOOK_HOLD_NOT_FOUND, ex.getErrorType());
        verify(bookBorrowRepository, never()).save(any());
    }

    @Test
    @DisplayName("대출 실패 - AVAILABLE 상태가 아닌 경우")
    void borrow_fail_notAvailable() {
        // Given
        BookHold bookHold = BookHold.create(BOOK_ID);
        bookHold.updateStatus(BookHoldStatus.BORROWED);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(mockUser));
        when(bookHoldRepository.findByIdForUpdate(BOOK_HOLD_ID)).thenReturn(Optional.of(bookHold));

        // When & Then
        CoreException ex = assertThrows(CoreException.class,
                () -> bookBorrowService.borrow(BOOK_HOLD_ID, USER_ID, "학습용"));
        assertEquals(ErrorType.BOOK_NOT_AVAILABLE, ex.getErrorType());
        verify(bookBorrowRepository, never()).save(any());
    }

    @Test
    @DisplayName("대출 실패 - 10권 초과")
    void borrow_fail_limitExceeded() {
        // Given
        BookHold bookHold = BookHold.create(BOOK_ID);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(mockUser));
        when(bookBorrowRepository.countByUserIdAndStatus(USER_ID, BorrowStatus.BORROWED)).thenReturn(10);

        // When & Then
        CoreException ex = assertThrows(CoreException.class,
                () -> bookBorrowService.borrow(BOOK_HOLD_ID, USER_ID, "학습용"));
        assertEquals(ErrorType.BORROW_LIMIT_EXCEEDED, ex.getErrorType());
        verify(bookBorrowRepository, never()).save(any());
    }

    // ======================================================================
    // returnBook
    // ======================================================================

    @Test
    @DisplayName("반납 성공 - 예약 없음 → AVAILABLE 전환")
    void returnBook_success_noReservation() {
        // Given
        BookBorrow bookBorrow = BookBorrow.create(BOOK_HOLD_ID, BOOK_ID, USER_ID, "학습용");
        BookHold bookHold = BookHold.create(BOOK_ID);
        bookHold.updateStatus(BookHoldStatus.BORROWED);

        when(bookBorrowRepository.findById(BORROW_ID)).thenReturn(Optional.of(bookBorrow));
        when(bookHoldRepository.findById(BOOK_HOLD_ID)).thenReturn(Optional.of(bookHold));

        // When
        bookBorrowService.returnBook(BORROW_ID, USER_ID);

        // Then
        assertEquals(BorrowStatus.RETURNED, bookBorrow.getStatus());
        assertThat(bookBorrow.getReturnDate()).isNotNull();
        assertEquals(BookHoldStatus.AVAILABLE, bookHold.getStatus());
    }

    @Test
    @DisplayName("반납 성공 - 예약 있음 → RESERVE_HOLD 전환")
    void returnBook_success_withReservation() {
        // Given
        BookBorrow bookBorrow = BookBorrow.create(BOOK_HOLD_ID, BOOK_ID, USER_ID, "학습용");
        BookHold bookHold = BookHold.create(BOOK_ID);
        bookHold.updateStatus(BookHoldStatus.BORROWED);
        Reservation reservation = Reservation.create(BOOK_HOLD_ID, 200L, LocalDateTime.now().plusDays(3));

        when(bookBorrowRepository.findById(BORROW_ID)).thenReturn(Optional.of(bookBorrow));
        when(bookHoldRepository.findById(BOOK_HOLD_ID)).thenReturn(Optional.of(bookHold));
        when(reservationRepository.findFirstByBookHoldIdAndStatusOrderByCreatedAtAsc(BOOK_HOLD_ID, ReservationStatus.WAITING))
                .thenReturn(Optional.of(reservation));

        // When
        bookBorrowService.returnBook(BORROW_ID, USER_ID);

        // Then
        assertEquals(BorrowStatus.RETURNED, bookBorrow.getStatus());
        assertThat(bookBorrow.getReturnDate()).isNotNull();
        assertEquals(BookHoldStatus.RESERVE_HOLD, bookHold.getStatus());
    }

    @Test
    @DisplayName("반납 실패 - 존재하지 않는 borrowId")
    void returnBook_fail_notFound() {
        // Given
        when(bookBorrowRepository.findById(BORROW_ID)).thenReturn(Optional.empty());

        // When & Then
        CoreException ex = assertThrows(CoreException.class,
                () -> bookBorrowService.returnBook(BORROW_ID, USER_ID));
        assertEquals(ErrorType.BOOKBORROW_NOT_FOUND, ex.getErrorType());
    }

    @Test
    @DisplayName("반납 실패 - IDOR (다른 사용자의 대출 반납 시도)")
    void returnBook_fail_notOwner() {
        // Given
        Long attackerUserId = 999L;
        BookBorrow bookBorrow = BookBorrow.create(BOOK_HOLD_ID, BOOK_ID, USER_ID, "학습용");
        when(bookBorrowRepository.findById(BORROW_ID)).thenReturn(Optional.of(bookBorrow));

        // When & Then
        CoreException ex = assertThrows(CoreException.class,
                () -> bookBorrowService.returnBook(BORROW_ID, attackerUserId));
        assertEquals(ErrorType.BORROW_NOT_OWNER, ex.getErrorType());
    }

    @Test
    @DisplayName("반납 실패 - 이미 반납 완료된 대출")
    void returnBook_fail_alreadyReturned() {
        // Given
        BookBorrow bookBorrow = BookBorrow.create(BOOK_HOLD_ID, BOOK_ID, USER_ID, "학습용");
        bookBorrow.returnBook(); // 이미 반납 처리
        when(bookBorrowRepository.findById(BORROW_ID)).thenReturn(Optional.of(bookBorrow));

        // When & Then
        CoreException ex = assertThrows(CoreException.class,
                () -> bookBorrowService.returnBook(BORROW_ID, USER_ID));
        assertEquals(ErrorType.BORROW_ALREADY_RETURNED, ex.getErrorType());
    }
}
