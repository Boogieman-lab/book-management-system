package com.example.bookmanagementsystembo.bookHold.repository;

import com.example.bookmanagementsystembo.bookHold.entity.BookHold;
import com.example.bookmanagementsystembo.bookHold.enums.BookHoldStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookHoldRepository extends JpaRepository<BookHold, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT bh FROM BookHold bh WHERE bh.bookHoldId = :id")
    Optional<BookHold> findByIdForUpdate(@Param("id") Long id);

    List<BookHold> findByBookId(Long bookId);

    Optional<BookHold> findByBookHoldIdAndBookId(Long holdId, Long bookId);

    void deleteByBookId(Long bookId);

    /** bookId 기준 전체 재고 수량 */
    int countByBookId(Long bookId);

    /** bookId + 상태 기준 재고 수량 (예: AVAILABLE 카운트) */
    int countByBookIdAndStatus(Long bookId, BookHoldStatus status);
}
