package com.example.bookmanagementsystembo.bookHold.service;

import com.example.bookmanagementsystembo.book.dto.BookHoldCreateRequest;
import com.example.bookmanagementsystembo.bookHold.dto.BookHoldResponse;
import com.example.bookmanagementsystembo.bookHold.dto.BookHoldStatusUpdateRequest;
import com.example.bookmanagementsystembo.bookHold.entity.BookHold;
import com.example.bookmanagementsystembo.bookHold.enums.BookHoldStatus;
import com.example.bookmanagementsystembo.bookHold.repository.BookHoldRepository;
import com.example.bookmanagementsystembo.exception.CoreException;
import com.example.bookmanagementsystembo.exception.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("BookHoldService 관리자 기능 단위 테스트")
@ExtendWith(MockitoExtension.class)
class BookHoldAdminServiceTest {

    @Mock
    private BookHoldRepository bookHoldRepository;

    @InjectMocks
    private BookHoldService bookHoldService;

    // ──────────────────────────────────────────────────────────────
    // readAll / read
    // ──────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("readAll - 도서별 hold 목록 조회")
    class ReadAllTest {

        @Test
        @DisplayName("성공 - bookId에 속한 hold 목록을 반환한다")
        void readAll_success() {
            // Given
            Long bookId = 1L;
            BookHold hold1 = BookHold.createWithLocation(bookId, "A-101");
            BookHold hold2 = BookHold.createWithLocation(bookId, "A-102");
            when(bookHoldRepository.findByBookId(bookId)).thenReturn(List.of(hold1, hold2));

            // When
            List<BookHoldResponse> result = bookHoldService.readAll(bookId);

            // Then
            assertThat(result).hasSize(2);
            verify(bookHoldRepository).findByBookId(bookId);
        }

        @Test
        @DisplayName("성공 - hold가 없으면 빈 리스트를 반환한다")
        void readAll_empty() {
            // Given
            Long bookId = 99L;
            when(bookHoldRepository.findByBookId(bookId)).thenReturn(List.of());

            // When
            List<BookHoldResponse> result = bookHoldService.readAll(bookId);

            // Then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("read - 단건 hold 조회")
    class ReadTest {

        @Test
        @DisplayName("성공 - bookId와 holdId가 일치하는 hold를 반환한다")
        void read_success() {
            // Given
            Long bookId = 1L;
            Long holdId = 10L;
            BookHold hold = BookHold.createWithLocation(bookId, "B-201");
            when(bookHoldRepository.findByBookHoldIdAndBookId(holdId, bookId)).thenReturn(Optional.of(hold));

            // When
            BookHoldResponse result = bookHoldService.read(bookId, holdId);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.bookHoldStatus()).isEqualTo(BookHoldStatus.AVAILABLE);
            verify(bookHoldRepository).findByBookHoldIdAndBookId(holdId, bookId);
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 holdId면 BOOK_HOLD_NOT_FOUND 예외 발생")
        void read_notFound() {
            // Given
            Long bookId = 1L;
            Long holdId = 999L;
            when(bookHoldRepository.findByBookHoldIdAndBookId(holdId, bookId)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> bookHoldService.read(bookId, holdId))
                    .isInstanceOf(CoreException.class)
                    .satisfies(ex -> assertThat(((CoreException) ex).getErrorType())
                            .isEqualTo(ErrorType.BOOK_HOLD_NOT_FOUND));
        }
    }

    // ──────────────────────────────────────────────────────────────
    // addHold
    // ──────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("addHold - 재고 +1")
    class AddHoldTest {

        @Test
        @DisplayName("bookId와 location을 받아 AVAILABLE 상태의 BookHold를 생성한다")
        void addHold_createsAvailableHold() {
            Long bookId = 1L;
            BookHoldCreateRequest req = new BookHoldCreateRequest("A-101");
            BookHold saved = BookHold.createWithLocation(bookId, "A-101");
            when(bookHoldRepository.save(any(BookHold.class))).thenReturn(saved);

            BookHoldResponse result = bookHoldService.addHold(bookId, req);

            ArgumentCaptor<BookHold> captor = ArgumentCaptor.forClass(BookHold.class);
            verify(bookHoldRepository).save(captor.capture());
            assertThat(captor.getValue().getStatus()).isEqualTo(BookHoldStatus.AVAILABLE);
            assertThat(captor.getValue().getBookId()).isEqualTo(bookId);
        }

        @Test
        @DisplayName("location이 null이어도 BookHold가 정상 생성된다")
        void addHold_withNullLocation_succeeds() {
            Long bookId = 2L;
            BookHoldCreateRequest req = new BookHoldCreateRequest(null);
            BookHold saved = BookHold.createWithLocation(bookId, null);
            when(bookHoldRepository.save(any(BookHold.class))).thenReturn(saved);

            bookHoldService.addHold(bookId, req);

            verify(bookHoldRepository).save(any(BookHold.class));
        }
    }

    // ──────────────────────────────────────────────────────────────
    // updateHoldStatus
    // ──────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("updateHoldStatus - 상태 전환")
    class UpdateHoldStatusTest {

        @Test
        @DisplayName("AVAILABLE 상태를 LOST로 변경할 수 있다")
        void available_to_lost_succeeds() {
            BookHold hold = BookHold.createWithLocation(1L, null);
            when(bookHoldRepository.findById(10L)).thenReturn(Optional.of(hold));

            BookHoldResponse result = bookHoldService.updateHoldStatus(10L, new BookHoldStatusUpdateRequest(BookHoldStatus.LOST, null));

            assertThat(result.bookHoldStatus()).isEqualTo(BookHoldStatus.LOST);
        }

        @Test
        @DisplayName("AVAILABLE 상태를 DISCARDED로 변경할 수 있다")
        void available_to_discarded_succeeds() {
            BookHold hold = BookHold.createWithLocation(1L, null);
            when(bookHoldRepository.findById(10L)).thenReturn(Optional.of(hold));

            BookHoldResponse result = bookHoldService.updateHoldStatus(10L, new BookHoldStatusUpdateRequest(BookHoldStatus.DISCARDED, null));

            assertThat(result.bookHoldStatus()).isEqualTo(BookHoldStatus.DISCARDED);
        }

        @Test
        @DisplayName("[예외] BORROWED 상태인 도서를 LOST로 전환하면 400 예외를 발생시킨다")
        void borrowed_to_lost_throwsException() {
            BookHold hold = buildHoldWithStatus(1L, BookHoldStatus.BORROWED);
            when(bookHoldRepository.findById(20L)).thenReturn(Optional.of(hold));

            assertThatThrownBy(() ->
                    bookHoldService.updateHoldStatus(20L, new BookHoldStatusUpdateRequest(BookHoldStatus.LOST, null)))
                    .isInstanceOf(CoreException.class)
                    .satisfies(ex -> assertThat(((CoreException) ex).getErrorType())
                            .isEqualTo(ErrorType.BOOK_HOLD_CANNOT_CHANGE_BORROWED));
        }

        @Test
        @DisplayName("[예외] BORROWED 상태인 도서를 DISCARDED로 전환하면 400 예외를 발생시킨다")
        void borrowed_to_discarded_throwsException() {
            BookHold hold = buildHoldWithStatus(1L, BookHoldStatus.BORROWED);
            when(bookHoldRepository.findById(20L)).thenReturn(Optional.of(hold));

            assertThatThrownBy(() ->
                    bookHoldService.updateHoldStatus(20L, new BookHoldStatusUpdateRequest(BookHoldStatus.DISCARDED, null)))
                    .isInstanceOf(CoreException.class)
                    .satisfies(ex -> assertThat(((CoreException) ex).getErrorType())
                            .isEqualTo(ErrorType.BOOK_HOLD_CANNOT_CHANGE_BORROWED));
        }

        @Test
        @DisplayName("존재하지 않는 bookHoldId면 BOOK_HOLD_NOT_FOUND 예외를 발생시킨다")
        void notFound_throwsException() {
            when(bookHoldRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() ->
                    bookHoldService.updateHoldStatus(999L, new BookHoldStatusUpdateRequest(BookHoldStatus.LOST, null)))
                    .isInstanceOf(CoreException.class)
                    .satisfies(ex -> assertThat(((CoreException) ex).getErrorType())
                            .isEqualTo(ErrorType.BOOK_HOLD_NOT_FOUND));
        }
    }

    // helper: status를 직접 설정한 BookHold 생성 (Reflection 사용)
    private BookHold buildHoldWithStatus(Long bookId, BookHoldStatus status) {
        BookHold hold = BookHold.createWithLocation(bookId, null);
        hold.updateStatus(status);
        return hold;
    }
}
