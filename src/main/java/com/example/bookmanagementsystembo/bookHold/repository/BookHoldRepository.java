package com.example.bookmanagementsystembo.bookHold.repository;

import com.example.bookmanagementsystembo.bookHold.entity.BookHold;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookHoldRepository extends JpaRepository<BookHold, Long> {
    @Query(
            value = "select bookHold.bookHoldId, bookHold.bookId, bookHold.bookHoldStatus, bookHold.location" +
                    "bookHold.createdAt, bookHold.updatedAt" +
                    "from book_hold" +
                    "where bookHold.bookId = :bookId"
            ,
            nativeQuery = true
    )
    List<BookHold> findAll(@Param("bookId") Long bookId);

    Optional<BookHold> findByBookHoldIdAndBookId(Long holdId, Long bookId);

    void deleteByBookId(Long bookId);
}
