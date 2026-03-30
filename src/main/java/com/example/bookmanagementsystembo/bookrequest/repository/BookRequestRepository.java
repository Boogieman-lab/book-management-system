package com.example.bookmanagementsystembo.bookRequest.repository;

import com.example.bookmanagementsystembo.bookRequest.entity.BookRequest;
import com.example.bookmanagementsystembo.bookRequest.enums.BookRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookRequestRepository extends JpaRepository<BookRequest, Long>, BookRequestQueryRepository {

    /** 주어진 상태의 전체 희망도서 신청 건수를 반환합니다. */
    long countByStatus(BookRequestStatus status);

    /** ISBN과 상태로 희망도서 신청 목록을 조회합니다 (입고 처리 시 ARRIVED 전환용). */
    List<BookRequest> findByIsbnAndStatus(String isbn, BookRequestStatus status);


    @Query(
            value = "SELECT br.* " +
                    "FROM book_request br " +
                    "ORDER BY br.book_request_id DESC " +
                    "LIMIT :limit OFFSET :offset",
            nativeQuery = true
    )
    List<BookRequest> findAll(
            @Param("offset") Long offset,
            @Param("limit") Long limit
    );


    @Query(
            value = "SELECT COUNT(*) FROM book_request",
            nativeQuery = true
    )
    Long count(
            @Param("limit") Long limit
    );
}
