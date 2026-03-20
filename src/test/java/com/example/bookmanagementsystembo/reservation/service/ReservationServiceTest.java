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
import com.example.bookmanagementsystembo.reservation.presentation.dto.ReservationResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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

    // ======================================================================
    // createReservation
    // ======================================================================

    @Nested
    @DisplayName("createReservation")
    class CreateReservation {

        @Test
        @DisplayName("성공 - BORROWED 상태 hold가 있고 예약 조건 모두 충족")
        void success() {
            // Given
            Long bookId = 1L;
            Long userId = 100L;
            BookHold bookHold = BookHold.create(bookId);
            bookHold.updateStatus(BookHoldStatus.BORROWED);

            when(bookHoldRepository.findByBookId(bookId)).thenReturn(List.of(bookHold));
            when(reservationRepository.existsByBookHold_BookHoldIdInAndUserIdAndStatus(
                    anyList(), eq(userId), eq(ReservationStatus.WAITING))).thenReturn(false);
            when(reservationRepository.countByUserIdAndStatus(userId, ReservationStatus.WAITING)).thenReturn(0);
            when(reservationRepository.countByBookHold_BookHoldIdAndStatus(
                    bookHold.getBookHoldId(), ReservationStatus.WAITING)).thenReturn(0);
            when(reservationRepository.save(any(Reservation.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            // When
            ReservationResponse result = reservationService.createReservation(bookId, userId);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.status()).isEqualTo(ReservationStatus.WAITING);
            verify(reservationRepository).save(any(Reservation.class));
        }

        @Test
        @DisplayName("실패 - AVAILABLE 상태 hold 존재 시 예약 불가 (대출 가능)")
        void fail_bookAvailable() {
            // Given
            Long bookId = 1L;
            Long userId = 100L;
            BookHold bookHold = BookHold.create(bookId); // 기본 상태 AVAILABLE

            when(bookHoldRepository.findByBookId(bookId)).thenReturn(List.of(bookHold));

            // When & Then
            CoreException ex = assertThrows(CoreException.class,
                    () -> reservationService.createReservation(bookId, userId));
            assertEquals(ErrorType.BOOK_AVAILABLE_NO_RESERVATION, ex.getErrorType());
            verify(reservationRepository, never()).save(any());
        }

        @Test
        @DisplayName("실패 - 동일 도서에 대해 이미 WAITING 예약 존재 (중복 예약)")
        void fail_alreadyExists() {
            // Given
            Long bookId = 1L;
            Long userId = 100L;
            BookHold bookHold = BookHold.create(bookId);
            bookHold.updateStatus(BookHoldStatus.BORROWED);

            when(bookHoldRepository.findByBookId(bookId)).thenReturn(List.of(bookHold));
            when(reservationRepository.existsByBookHold_BookHoldIdInAndUserIdAndStatus(
                    anyList(), eq(userId), eq(ReservationStatus.WAITING))).thenReturn(true);

            // When & Then
            CoreException ex = assertThrows(CoreException.class,
                    () -> reservationService.createReservation(bookId, userId));
            assertEquals(ErrorType.RESERVATION_ALREADY_EXISTS, ex.getErrorType());
            verify(reservationRepository, never()).save(any());
        }

        @Test
        @DisplayName("실패 - 사용자 WAITING 예약이 이미 2건 이상 (유저 예약 한도 초과)")
        void fail_userReservationLimitExceeded() {
            // Given
            Long bookId = 1L;
            Long userId = 100L;
            BookHold bookHold = BookHold.create(bookId);
            bookHold.updateStatus(BookHoldStatus.BORROWED);

            when(bookHoldRepository.findByBookId(bookId)).thenReturn(List.of(bookHold));
            when(reservationRepository.existsByBookHold_BookHoldIdInAndUserIdAndStatus(
                    anyList(), eq(userId), eq(ReservationStatus.WAITING))).thenReturn(false);
            when(reservationRepository.countByUserIdAndStatus(userId, ReservationStatus.WAITING)).thenReturn(2);

            // When & Then
            CoreException ex = assertThrows(CoreException.class,
                    () -> reservationService.createReservation(bookId, userId));
            assertEquals(ErrorType.USER_RESERVATION_LIMIT_EXCEEDED, ex.getErrorType());
            verify(reservationRepository, never()).save(any());
        }

        @Test
        @DisplayName("실패 - 모든 hold의 WAITING 예약이 이미 2명 (도서별 예약 한도 초과)")
        void fail_reservationLimitExceeded() {
            // Given
            Long bookId = 1L;
            Long userId = 100L;
            BookHold bookHold = BookHold.create(bookId);
            bookHold.updateStatus(BookHoldStatus.BORROWED);

            when(bookHoldRepository.findByBookId(bookId)).thenReturn(List.of(bookHold));
            when(reservationRepository.existsByBookHold_BookHoldIdInAndUserIdAndStatus(
                    anyList(), eq(userId), eq(ReservationStatus.WAITING))).thenReturn(false);
            when(reservationRepository.countByUserIdAndStatus(userId, ReservationStatus.WAITING)).thenReturn(0);
            when(reservationRepository.countByBookHold_BookHoldIdAndStatus(
                    bookHold.getBookHoldId(), ReservationStatus.WAITING)).thenReturn(2);

            // When & Then
            CoreException ex = assertThrows(CoreException.class,
                    () -> reservationService.createReservation(bookId, userId));
            assertEquals(ErrorType.RESERVATION_LIMIT_EXCEEDED, ex.getErrorType());
            verify(reservationRepository, never()).save(any());
        }

        @Test
        @DisplayName("실패 - 해당 bookId의 BookHold가 존재하지 않음 (예약 가능한 hold 없음)")
        void fail_noBookHolds() {
            // Given
            Long bookId = 1L;
            Long userId = 100L;

            when(bookHoldRepository.findByBookId(bookId)).thenReturn(List.of());

            // When & Then
            // bookHolds가 비어 있으면: AVAILABLE 없음 → 중복 체크 skip → userWaiting 체크 → targetHold = null → RESERVATION_LIMIT_EXCEEDED
            when(reservationRepository.countByUserIdAndStatus(userId, ReservationStatus.WAITING)).thenReturn(0);

            CoreException ex = assertThrows(CoreException.class,
                    () -> reservationService.createReservation(bookId, userId));
            assertEquals(ErrorType.RESERVATION_LIMIT_EXCEEDED, ex.getErrorType());
            verify(reservationRepository, never()).save(any());
        }
    }

    // ======================================================================
    // cancelReservation
    // ======================================================================

    @Nested
    @DisplayName("cancelReservation")
    class CancelReservation {

        @Test
        @DisplayName("성공 - WAITING 예약을 EXPIRED로 만료 처리")
        void success() {
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
        @DisplayName("실패 - 존재하지 않는 예약 ID")
        void fail_notFound() {
            // Given
            Long reservationId = 999L;
            Long userId = 100L;

            when(reservationRepository.findById(reservationId)).thenReturn(Optional.empty());

            // When & Then
            CoreException ex = assertThrows(CoreException.class,
                    () -> reservationService.cancelReservation(reservationId, userId));
            assertEquals(ErrorType.RESERVATION_NOT_FOUND, ex.getErrorType());
        }

        @Test
        @DisplayName("실패 - 예약 소유자가 아닌 사용자가 취소 시도 (IDOR)")
        void fail_notOwner() {
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
            assertEquals(ReservationStatus.WAITING, reservation.getStatus()); // 상태 변경 없음
        }

        @Test
        @DisplayName("실패 - 이미 EXPIRED 상태인 예약 재취소 불가")
        void fail_alreadyExpired() {
            // Given
            Long reservationId = 1L;
            Long userId = 100L;
            BookHold bookHold = BookHold.create(10L);
            Reservation reservation = Reservation.create(bookHold, userId, null);
            reservation.expire(); // 이미 만료

            when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));

            // When & Then
            CoreException ex = assertThrows(CoreException.class,
                    () -> reservationService.cancelReservation(reservationId, userId));
            assertEquals(ErrorType.RESERVATION_ALREADY_CANCELLED, ex.getErrorType());
        }
    }
}
