package com.example.bookmanagementsystembo.bookBorrow.repository;

import com.example.bookmanagementsystembo.bookBorrow.entity.BookBorrow;
import com.example.bookmanagementsystembo.bookBorrow.enums.BorrowStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookBorrowRepository extends JpaRepository<BookBorrow, Long>, BookBorrowQueryRepository {
    BookBorrow findTopByBookHoldIdOrderByCreatedAtDesc(Long bookHoldId);

    int countBybookHoldId(Long bookId);

    int countByUserIdAndStatus(Long userId, BorrowStatus status);
}
