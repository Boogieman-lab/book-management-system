package com.example.bookmanagementsystembo.bookrequest.service;

import com.example.bookmanagementsystembo.book.repository.BookRepository;
import com.example.bookmanagementsystembo.bookrequest.dto.*;
import com.example.bookmanagementsystembo.bookrequest.entity.BookRequest;
import com.example.bookmanagementsystembo.bookrequest.repository.BookRequestRepository;
import com.example.bookmanagementsystembo.exception.CoreException;
import com.example.bookmanagementsystembo.exception.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookRequestServiceV1Test {

    @InjectMocks
    private BookRequestService bookRequestService;

    @Mock
    private BookRequestRepository bookRequestRepository;

    @Mock
    private BookRepository bookRepository;

    @Test
    @DisplayName("희망 도서 신청 성공")
    void createV1_success() {
        // Given
        Long userId = 1L;
        BookRequestCreateReq request = new BookRequestCreateReq("이펙티브 자바", "조슈아 블로크", "인사이트", "9788966261549", "업무에 필요");
        BookRequest saved = BookRequest.create(userId, request.title(), request.authors(), request.publisher(), request.isbn(), request.reason());

        when(bookRepository.existsByIsbn(request.isbn())).thenReturn(false);
        when(bookRequestRepository.existsByIsbnAndStatus(request.isbn(), BookRequestStatus.PENDING)).thenReturn(false);
        when(bookRequestRepository.save(any(BookRequest.class))).thenReturn(saved);

        // When
        BookRequestV1Res result = bookRequestService.createV1(userId, request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.title()).isEqualTo("이펙티브 자바");
        assertThat(result.status()).isEqualTo(BookRequestStatus.PENDING);
        verify(bookRepository).existsByIsbn(request.isbn());
        verify(bookRequestRepository).existsByIsbnAndStatus(request.isbn(), BookRequestStatus.PENDING);
        verify(bookRequestRepository).save(any(BookRequest.class));
    }

    @Test
    @DisplayName("희망 도서 신청 실패 - ISBN이 Book 테이블에 이미 존재")
    void createV1_fail_bookAlreadyExists() {
        // Given
        Long userId = 1L;
        BookRequestCreateReq request = new BookRequestCreateReq("이펙티브 자바", "조슈아 블로크", "인사이트", "9788966261549", "업무에 필요");

        when(bookRepository.existsByIsbn(request.isbn())).thenReturn(true);

        // When & Then
        CoreException ex = assertThrows(CoreException.class, () -> bookRequestService.createV1(userId, request));
        assertEquals(ErrorType.BOOK_REQUEST_ALREADY_EXISTS, ex.getErrorType());
        verify(bookRequestRepository, never()).save(any());
    }

    @Test
    @DisplayName("희망 도서 신청 실패 - PENDING 상태 중복 신청")
    void createV1_fail_duplicatePending() {
        // Given
        Long userId = 1L;
        BookRequestCreateReq request = new BookRequestCreateReq("이펙티브 자바", "조슈아 블로크", "인사이트", "9788966261549", "업무에 필요");

        when(bookRepository.existsByIsbn(request.isbn())).thenReturn(false);
        when(bookRequestRepository.existsByIsbnAndStatus(request.isbn(), BookRequestStatus.PENDING)).thenReturn(true);

        // When & Then
        CoreException ex = assertThrows(CoreException.class, () -> bookRequestService.createV1(userId, request));
        assertEquals(ErrorType.BOOK_REQUEST_DUPLICATE, ex.getErrorType());
        verify(bookRequestRepository, never()).save(any());
    }

    @Test
    @DisplayName("ISBN이 null이면 중복 체크 스킵하고 신청 성공")
    void createV1_success_nullIsbn() {
        // Given
        Long userId = 1L;
        BookRequestCreateReq request = new BookRequestCreateReq("새 도서", "저자", "출판사", null, "사유");
        BookRequest saved = BookRequest.create(userId, request.title(), request.authors(), request.publisher(), request.isbn(), request.reason());

        when(bookRequestRepository.save(any(BookRequest.class))).thenReturn(saved);

        // When
        BookRequestV1Res result = bookRequestService.createV1(userId, request);

        // Then
        assertThat(result).isNotNull();
        verify(bookRepository, never()).existsByIsbn(any());
        verify(bookRequestRepository, never()).existsByIsbnAndStatus(any(), any());
    }

    @Test
    @DisplayName("관리자 전체 조회 - userId 필터 없이 조회")
    void readAllV1_admin() {
        // Given
        BookRequest br = BookRequest.create(1L, "테스트 도서", "저자", "출판사", "1234", "사유");
        Page<BookRequest> page = new PageImpl<>(List.of(br), PageRequest.of(0, 10), 1);

        when(bookRequestRepository.findAllByCondition(null, null, PageRequest.of(0, 10))).thenReturn(page);

        // When
        BookRequestPageV1Res result = bookRequestService.readAllV1(1, 10, null, 1L, true);

        // Then
        assertThat(result.items()).hasSize(1);
        assertThat(result.totalElements()).isEqualTo(1);
        verify(bookRequestRepository).findAllByCondition(null, null, PageRequest.of(0, 10));
    }

    @Test
    @DisplayName("사용자 본인 조회 - userId 필터 적용")
    void readAllV1_user() {
        // Given
        Long userId = 2L;
        BookRequest br = BookRequest.create(userId, "테스트 도서", "저자", "출판사", "1234", "사유");
        Page<BookRequest> page = new PageImpl<>(List.of(br), PageRequest.of(0, 10), 1);

        when(bookRequestRepository.findAllByCondition(userId, null, PageRequest.of(0, 10))).thenReturn(page);

        // When
        BookRequestPageV1Res result = bookRequestService.readAllV1(1, 10, null, userId, false);

        // Then
        assertThat(result.items()).hasSize(1);
        assertThat(result.items().get(0).userId()).isEqualTo(userId);
        verify(bookRequestRepository).findAllByCondition(userId, null, PageRequest.of(0, 10));
    }

    @Test
    @DisplayName("승인 처리 성공")
    void updateStatus_approve() {
        // Given
        Long requestId = 1L;
        BookRequest br = BookRequest.create(1L, "테스트 도서", "저자", "출판사", "1234", "사유");

        when(bookRequestRepository.findById(requestId)).thenReturn(Optional.of(br));

        // When
        BookRequestV1Res result = bookRequestService.updateStatus(requestId, BookRequestStatus.APPROVED, null);

        // Then
        assertThat(result.status()).isEqualTo(BookRequestStatus.APPROVED);
        assertThat(result.rejectReason()).isNull();
    }

    @Test
    @DisplayName("거절 처리 성공")
    void updateStatus_reject() {
        // Given
        Long requestId = 1L;
        BookRequest br = BookRequest.create(1L, "테스트 도서", "저자", "출판사", "1234", "사유");

        when(bookRequestRepository.findById(requestId)).thenReturn(Optional.of(br));

        // When
        BookRequestV1Res result = bookRequestService.updateStatus(requestId, BookRequestStatus.REJECTED, "예산 부족");

        // Then
        assertThat(result.status()).isEqualTo(BookRequestStatus.REJECTED);
        assertThat(result.rejectReason()).isEqualTo("예산 부족");
    }

    @Test
    @DisplayName("이미 처리된 신청 변경 불가")
    void updateStatus_fail_alreadyProcessed() {
        // Given
        Long requestId = 1L;
        BookRequest br = BookRequest.create(1L, "테스트 도서", "저자", "출판사", "1234", "사유");
        br.updateStatus(BookRequestStatus.APPROVED, null);

        when(bookRequestRepository.findById(requestId)).thenReturn(Optional.of(br));

        // When & Then
        CoreException ex = assertThrows(CoreException.class,
                () -> bookRequestService.updateStatus(requestId, BookRequestStatus.REJECTED, "거절 사유"));
        assertEquals(ErrorType.BOOK_REQUEST_STATUS_ALREADY_PROCESSED, ex.getErrorType());
    }
}
