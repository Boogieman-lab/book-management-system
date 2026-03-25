package com.example.bookmanagementsystembo.bookRequest.service;

import com.example.bookmanagementsystembo.bookRequest.dto.*;
import com.example.bookmanagementsystembo.bookRequest.entity.BookRequest;
import com.example.bookmanagementsystembo.bookRequest.enums.BookRequestStatus;
import com.example.bookmanagementsystembo.bookRequest.repository.BookRequestRepository;
import com.example.bookmanagementsystembo.common.PageLimitCalculator;
import com.example.bookmanagementsystembo.exception.CoreException;
import com.example.bookmanagementsystembo.exception.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class BookRequestService {

    private final BookRequestRepository bookRequestRepository;

    /** 희망 도서를 신청합니다 (레거시 API — 중복 검증 없음). */
    public BookRequestSummaryResponse create(Long userId, BookRequestCreateRequest request) {

        BookRequest bookRequest = bookRequestRepository.save(
                BookRequest.create(
                        userId, request.title(),request.authors(),
                        request.publisher(), request.isbn(), request.reason()
                )
        );
        return BookRequestSummaryResponse.from(bookRequest);
    }

    /** 전체 희망 도서 신청 목록을 페이지네이션으로 조회합니다 (레거시 API). */
    public BookRequestSummaryPageResponse readAll(Long page, Long pageSize) {
        List<BookRequest> bookRequests = bookRequestRepository.findAll((page - 1) * pageSize, pageSize);
        Long bookRequestCount = bookRequestRepository.count(
                PageLimitCalculator.calculatePageLimit(page, pageSize, 10L)
        );
        return BookRequestSummaryPageResponse.of(bookRequests.stream().map(BookRequestSummaryResponse::from).toList(), bookRequestCount);
    }

    /** 신청 ID로 단건 희망 도서 신청 정보를 조회합니다. */
    public BookRequestSummaryResponse read(Long bookRequestId) {
        BookRequest bookRequest = bookRequestRepository.findById(bookRequestId)
                .orElseThrow(() -> new CoreException(ErrorType.BOOK_REQUEST_FOUND, bookRequestId));

        return BookRequestSummaryResponse.from(bookRequest);
    }

    /** 희망 도서 신청 정보를 수정합니다 (레거시 API). */
    @Transactional
    public BookRequestSummaryResponse update(Long bookRequestId, BookRequestUpdateRequest request) {
        BookRequest bookRequest = bookRequestRepository.findById(bookRequestId)
                .orElseThrow(()->new CoreException(ErrorType.BOOK_REQUEST_FOUND, bookRequestId));
        bookRequest.update(request.title(), request.authors(), request.publisher(), request.isbn(), request.reason());
        return BookRequestSummaryResponse.from(bookRequest);
    }

    /**
     * 희망 도서를 신청합니다 (V1 API).
     * 동일 ISBN으로 PENDING 상태인 신청이 이미 있으면 중복 신청을 차단합니다.
     * 기보유 도서여도 신청은 허용합니다.
     */
    @Transactional
    public BookRequestResponse createV1(Long userId, BookRequestCreateRequest request) {
        if (StringUtils.hasText(request.isbn())) {
            if (bookRequestRepository.existsByIsbnAndStatus(request.isbn(), BookRequestStatus.PENDING)) {
                throw new CoreException(ErrorType.BOOK_REQUEST_DUPLICATE, request.isbn());
            }
        }

        BookRequest bookRequest = bookRequestRepository.save(
                BookRequest.create(
                        userId, request.title(), request.authors(),
                        request.publisher(), request.isbn(), request.reason()
                )
        );
        return BookRequestResponse.from(bookRequest);
    }

    /**
     * 희망 도서 신청 목록을 조회합니다 (V1 API).
     * 관리자는 전체 조회, 일반 사용자는 본인 신청 내역만 조회합니다.
     * status가 null이면 전체 상태를 조회합니다.
     */
    public BookRequestPageResponse readAllV1(int page, int size, BookRequestStatus status, Long userId, boolean isAdmin) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Long filterUserId = isAdmin ? null : userId;

        Page<BookRequest> result = bookRequestRepository.findAllByCondition(filterUserId, status, pageable);

        List<BookRequestResponse> items = result.getContent().stream()
                .map(BookRequestResponse::from)
                .toList();

        return BookRequestPageResponse.of(items, result.getTotalElements(), result.getTotalPages(), page, size);
    }

    /**
     * 희망 도서 신청을 취소합니다 (V1 API).
     * PENDING 상태인 본인 신청만 취소 가능합니다.
     */
    @Transactional
    public void cancelV1(Long bookRequestId, Long userId) {
        BookRequest bookRequest = bookRequestRepository.findById(bookRequestId)
                .orElseThrow(() -> new CoreException(ErrorType.BOOK_REQUEST_FOUND, bookRequestId));

        if (!bookRequest.getUserId().equals(userId)) {
            throw new CoreException(ErrorType.BOOK_REQUEST_NOT_OWNER, bookRequestId);
        }

        if (bookRequest.getStatus() != BookRequestStatus.PENDING) {
            throw new CoreException(ErrorType.BOOK_REQUEST_STATUS_ALREADY_PROCESSED, bookRequestId);
        }

        bookRequestRepository.deleteById(bookRequestId);
    }

    /**
     * 희망 도서 신청 상태를 변경합니다 (관리자 승인/거절).
     * PENDING 상태인 신청만 변경 가능하며, 이미 처리된 신청은 예외를 발생시킵니다.
     */
    @Transactional
    public BookRequestResponse updateStatus(Long requestId, BookRequestStatus status, String rejectReason) {
        BookRequest bookRequest = bookRequestRepository.findById(requestId)
                .orElseThrow(() -> new CoreException(ErrorType.BOOK_REQUEST_FOUND, requestId));

        if (bookRequest.getStatus() != BookRequestStatus.PENDING) {
            throw new CoreException(ErrorType.BOOK_REQUEST_STATUS_ALREADY_PROCESSED, requestId);
        }

        bookRequest.updateStatus(status, rejectReason);
        return BookRequestResponse.from(bookRequest);
    }

}
