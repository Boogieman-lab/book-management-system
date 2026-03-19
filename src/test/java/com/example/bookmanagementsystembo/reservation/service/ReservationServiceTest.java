package com.example.bookmanagementsystembo.reservation.service;

import com.example.bookmanagementsystembo.bookHold.entity.BookHold;
import com.example.bookmanagementsystembo.bookHold.enums.BookHoldStatus;
import com.example.bookmanagementsystembo.bookHold.repository.BookHoldRepository;
import com.example.bookmanagementsystembo.exception.CoreException;
import com.example.bookmanagementsystembo.exception.ErrorType;
import com.example.bookmanagementsystembo.reservation.domain.entity.Reservation;
import com.example.bookmanagementsystembo.reservation.domain.service.ReservationService;
import com.example.bookmanagementsystembo.reservation.enums.ReservationStatus;
import com.example.bookmanagementsystembo.reservation.infra.ReservationRepository;
import com.example.bookmanagementsystembo.reservation.presentation.dto.ReservationRes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @InjectMocks
    private ReservationService reservationService;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private BookHoldRepository bookHoldRepository;

    // ========== 예약 등록 테스트 ==========

    @Test
    @DisplayName("예약 성공")
    void createReservation_success() {
        // Given
        Long bookId = 1L;
        Long userId = 100L;
        BookHold bookHold = BookHold.create(bookId);
        bookHold.updateStatus(BookHoldStatus.BORROWED);

        when(bookHoldRepository.findByBookId(bookId)).thenReturn(List.of(bookHold));
        when(reservationRepository.existsByBookHold_BookHoldIdInAndUserIdAndStatus(anyList(), eq(userId), eq(ReservationStatus.WAITING)))
                .thenReturn(false);
        when(reservationRepository.countByUserIdAndStatus(userId, ReservationStatus.WAITING)).thenReturn(0);
        when(reservationRepository.countByBookHold_BookHoldIdAndStatus(bookHold.getBookHoldId(), ReservationStatus.WAITING)).thenReturn(0);
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        ReservationRes res = reservationService.createReservation(bookId, userId);

        // Then
        assertThat(res).isNotNull();
        assertThat(res.status()).isEqualTo(ReservationStatus.WAITING);
        verify(reservationRepository).save(any(Reservation.class));
    }

    @Test
    @DisplayName("대출 가능 도서 예약 불가")
    void createReservation_fail_bookAvailable() {
        // Given
        Long bookId = 1L;
        Long userId = 100L;
        BookHold bookHold = BookHold.create(bookId); // AVAILABLE 상태

        when(bookHoldRepository.findByBookId(bookId)).thenReturn(List.of(bookHold));

        // When & Then
        CoreException ex = assertThrows(CoreException.class,
                () -> reservationService.createReservation(bookId, userId));
        assertEquals(ErrorType.BOOK_AVAILABLE_NO_RESERVATION, ex.getErrorType());
    }

    @Test
    @DisplayName("도서별 예약 2명 초과 불가")
    void createReservation_fail_reservationLimitExceeded() {
        // Given
        Long bookId = 1L;
        Long userId = 100L;
        BookHold bookHold = BookHold.create(bookId);
        bookHold.updateStatus(BookHoldStatus.BORROWED);

        when(bookHoldRepository.findByBookId(bookId)).thenReturn(List.of(bookHold));
        when(reservationRepository.existsByBookHold_BookHoldIdInAndUserIdAndStatus(anyList(), eq(userId), eq(ReservationStatus.WAITING)))
                .thenReturn(false);
        when(reservationRepository.countByUserIdAndStatus(userId, ReservationStatus.WAITING)).thenReturn(0);
        when(reservationRepository.countByBookHold_BookHoldIdAndStatus(bookHold.getBookHoldId(), ReservationStatus.WAITING)).thenReturn(2);

        // When & Then
        CoreException ex = assertThrows(CoreException.class,
                () -> reservationService.createReservation(bookId, userId));
        assertEquals(ErrorType.RESERVATION_LIMIT_EXCEEDED, ex.getErrorType());
    }

    @Test
    @DisplayName("동일 도서 중복 예약 불가")
    void createReservation_fail_alreadyExists() {
        // Given
        Long bookId = 1L;
        Long userId = 100L;
        BookHold bookHold = BookHold.create(bookId);
        bookHold.updateStatus(BookHoldStatus.BORROWED);

        when(bookHoldRepository.findByBookId(bookId)).thenReturn(List.of(bookHold));
        when(reservationRepository.existsByBookHold_BookHoldIdInAndUserIdAndStatus(anyList(), eq(userId), eq(ReservationStatus.WAITING)))
                .thenReturn(true);

        // When & Then
        CoreException ex = assertThrows(CoreException.class,
                () -> reservationService.createReservation(bookId, userId));
        assertEquals(ErrorType.RESERVATION_ALREADY_EXISTS, ex.getErrorType());
    }

    @Test
    @DisplayName("1인 예약 2권 초과 불가")
    void createReservation_fail_userReservationLimitExceeded() {
        // Given
        Long bookId = 1L;
        Long userId = 100L;
        BookHold bookHold = BookHold.create(bookId);
        bookHold.updateStatus(BookHoldStatus.BORROWED);

        when(bookHoldRepository.findByBookId(bookId)).thenReturn(List.of(bookHold));
        when(reservationRepository.existsByBookHold_BookHoldIdInAndUserIdAndStatus(anyList(), eq(userId), eq(ReservationStatus.WAITING)))
                .thenReturn(false);
        when(reservationRepository.countByUserIdAndStatus(userId, ReservationStatus.WAITING)).thenReturn(2);

        // When & Then
        CoreException ex = assertThrows(CoreException.class,
                () -> reservationService.createReservation(bookId, userId));
        assertEquals(ErrorType.USER_RESERVATION_LIMIT_EXCEEDED, ex.getErrorType());
    }

    // ========== 예약 취소 테스트 ==========

    @Test
    @DisplayName("예약 취소 성공")
    void cancelReservation_success() {
        // Given
        Long reservationId = 1L;
        Long userId = 100L;
        BookHold bookHold = BookHold.create(10L);
        Reservation reservation = Reservation.create(bookHold, userId, null);

        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));

        // When
        reservationService.cancelReservation(reservationId, userId);

        // Then
        assertEquals(ReservationStatus.EXPIRED, reservation.getStatus());
    }

    @Test
    @DisplayName("타인 예약 취소 불가 (IDOR)")
    void cancelReservation_fail_notOwner() {
        // Given
        Long reservationId = 1L;
        Long ownerUserId = 100L;
        Long attackerUserId = 999L;
        BookHold bookHold = BookHold.create(10L);
        Reservation reservation = Reservation.create(bookHold, ownerUserId, null);

        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));

        // When & Then
        CoreException ex = assertThrows(CoreException.class,
                () -> reservationService.cancelReservation(reservationId, attackerUserId));
        assertEquals(ErrorType.RESERVATION_NOT_OWNER, ex.getErrorType());
    }

    @Test
    @DisplayName("이미 만료된 예약 취소 불가")
    void cancelReservation_fail_alreadyExpired() {
        // Given
        Long reservationId = 1L;
        Long userId = 100L;
        BookHold bookHold = BookHold.create(10L);
        Reservation reservation = Reservation.create(bookHold, userId, null);
        reservation.expire(); // 이미 만료 처리

        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));

        // When & Then
        CoreException ex = assertThrows(CoreException.class,
                () -> reservationService.cancelReservation(reservationId, userId));
        assertEquals(ErrorType.RESERVATION_ALREADY_CANCELLED, ex.getErrorType());
    }
}
