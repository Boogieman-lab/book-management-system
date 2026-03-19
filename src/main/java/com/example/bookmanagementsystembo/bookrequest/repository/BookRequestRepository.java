package com.example.bookmanagementsystembo.bookRequest.repository;

import com.example.bookmanagementsystembo.bookRequest.entity.BookRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookRequestRepository extends JpaRepository<BookRequest, Long>, BookRequestQueryRepository {

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
