package com.example.bookmanagementsystembo.bookRequest.service;

import com.example.bookmanagementsystembo.book.repository.BookRepository;
import com.example.bookmanagementsystembo.bookRequest.dto.*;
import com.example.bookmanagementsystembo.bookRequest.entity.BookRequest;
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
    private final BookRepository bookRepository;

    public BookRequestSummaryResponse create(Long userId, BookRequestCreateRequest request) {

        BookRequest bookRequest = bookRequestRepository.save(
                BookRequest.create(
                        userId, request.title(),request.authors(),
                        request.publisher(), request.isbn(), request.reason()
                )
        );
        return BookRequestSummaryResponse.from(bookRequest);
    }

    public BookRequestSummaryPageResponse readAll(Long page, Long pageSize) {
        List<BookRequest> bookRequests = bookRequestRepository.findAll((page - 1) * pageSize, pageSize);
        Long bookRequestCount = bookRequestRepository.count(
                PageLimitCalculator.calculatePageLimit(page, pageSize, 10L)
        );
        return BookRequestSummaryPageResponse.of(bookRequests.stream().map(BookRequestSummaryResponse::from).toList(), bookRequestCount);
    }

    public BookRequestSummaryResponse read(Long bookRequestId) {
        BookRequest bookRequest = bookRequestRepository.findById(bookRequestId)
                .orElseThrow(() -> new CoreException(ErrorType.BOOK_REQUEST_FOUND, bookRequestId));

        return BookRequestSummaryResponse.from(bookRequest);
    }

    @Transactional
    public BookRequestSummaryResponse update(Long bookRequestId, BookRequestUpdateRequest request) {
        BookRequest bookRequest = bookRequestRepository.findById(bookRequestId)
                .orElseThrow(()->new CoreException(ErrorType.BOOK_REQUEST_FOUND, bookRequestId));
        bookRequest.update(request.title(), request.authors(), request.publisher(), request.isbn(), request.reason());
        return BookRequestSummaryResponse.from(bookRequest);
    }

    @Transactional
    public BookRequestResponse createV1(Long userId, BookRequestCreateRequest request) {
        if (StringUtils.hasText(request.isbn())) {
            if (bookRepository.existsByIsbn(request.isbn())) {
                throw new CoreException(ErrorType.BOOK_REQUEST_ALREADY_EXISTS, request.isbn());
            }
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

    public BookRequestPageResponse readAllV1(int page, int size, BookRequestStatus status, Long userId, boolean isAdmin) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Long filterUserId = isAdmin ? null : userId;

        Page<BookRequest> result = bookRequestRepository.findAllByCondition(filterUserId, status, pageable);

        List<BookRequestResponse> items = result.getContent().stream()
                .map(BookRequestResponse::from)
                .toList();

        return BookRequestPageResponse.of(items, result.getTotalElements(), result.getTotalPages(), page, size);
    }

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
