package com.example.bookmanagementsystembo.bookHold.repository;

import com.example.bookmanagementsystembo.bookHold.entity.BookHold;
import com.example.bookmanagementsystembo.bookHold.enums.BookHoldStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookHoldRepository extends JpaRepository<BookHold, Long> {

    List<BookHold> findByBookId(Long bookId);

    Optional<BookHold> findByBookHoldIdAndBookId(Long holdId, Long bookId);

    void deleteByBookId(Long bookId);

    /** bookId 기준 전체 재고 수량 */
    int countByBookId(Long bookId);

    /** bookId + 상태 기준 재고 수량 (예: AVAILABLE 카운트) */
    int countByBookIdAndStatus(Long bookId, BookHoldStatus status);
}
