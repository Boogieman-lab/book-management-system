package com.example.bookmanagementsystembo.bookRequest.repository;

import com.example.bookmanagementsystembo.bookRequest.enums.BookRequestStatus;
import com.example.bookmanagementsystembo.bookRequest.entity.BookRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookRequestQueryRepository {
    Page<BookRequest> findAllByCondition(Long userId, BookRequestStatus status, Pageable pageable);
    boolean existsByIsbnAndStatus(String isbn, BookRequestStatus status);
}
