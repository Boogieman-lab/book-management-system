package com.example.bookmanagementsystembo.bookBorrow.infra;

import com.example.bookmanagementsystembo.bookBorrow.domain.entity.BookBorrow;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookBorrowRepository extends JpaRepository<BookBorrow, Long>, BookBorrowQueryRepository {
    BookBorrow findTopByBookHoldIdOrderByCreatedAtDesc(Long bookHoldId);

    int countBybookHoldId(Long bookId);
}
