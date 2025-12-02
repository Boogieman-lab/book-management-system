package com.example.bookmanagementsystembo.bookrequest.service;

import com.example.bookmanagementsystembo.bookrequest.dto.BookRequestCreateReq;
import com.example.bookmanagementsystembo.bookrequest.dto.BookRequestPageRes;
import com.example.bookmanagementsystembo.bookrequest.dto.BookRequestRes;
import com.example.bookmanagementsystembo.bookrequest.dto.BookRequestUpdateReq;
import com.example.bookmanagementsystembo.bookrequest.entity.BookRequest;
import com.example.bookmanagementsystembo.bookrequest.repository.BookRequestRepository;
import com.example.bookmanagementsystembo.common.PageLimitCalculator;
import com.example.bookmanagementsystembo.exception.CoreException;
import com.example.bookmanagementsystembo.exception.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class BookRequestService {

    private final BookRequestRepository bookRequestRepository;

    public BookRequestRes create(BookRequestCreateReq request) {
        BookRequest bookRequest = bookRequestRepository.save(
                BookRequest.create(
                        request.userId(), request.title(),request.authors(),
                        request.publisher(), request.isbn(), request.reason()
                )
        );
        return BookRequestRes.from(bookRequest);
    }

    public BookRequestPageRes readAll(Long page, Long pageSize) {
        List<BookRequest> bookRequests = bookRequestRepository.findAll((page - 1) * pageSize, pageSize);
        Long bookRequestCount = bookRequestRepository.count(
                PageLimitCalculator.calculatePageLimit(page, pageSize, 10L)
        );
        return BookRequestPageRes.of(bookRequests.stream().map(BookRequestRes::from).toList(), bookRequestCount);
    }

    public BookRequestRes read(Long bookRequestId) {
        BookRequest bookRequest = bookRequestRepository.findById(bookRequestId)
                .orElseThrow(() -> new CoreException(ErrorType.BOOK_REQUEST_FOUND, bookRequestId));

        return BookRequestRes.from(bookRequest);
    }

    @Transactional
    public BookRequestRes update(Long bookRequestId, BookRequestUpdateReq request) {
        BookRequest bookRequest = bookRequestRepository.findById(bookRequestId)
                .orElseThrow(()->new CoreException(ErrorType.BOOK_REQUEST_FOUND, bookRequestId));
        bookRequest.update(request.title(), request.authors(), request.publisher(), request.isbn(), request.reason());
        return BookRequestRes.from(bookRequest);
    }

}
