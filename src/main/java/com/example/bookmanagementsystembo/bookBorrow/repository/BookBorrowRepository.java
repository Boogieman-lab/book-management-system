package com.example.bookmanagementsystembo.bookBorrow.repository;

import com.example.bookmanagementsystembo.bookBorrow.entity.BookBorrow;
import com.example.bookmanagementsystembo.bookBorrow.enums.BorrowStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookBorrowRepository extends JpaRepository<BookBorrow, Long>, BookBorrowQueryRepository {
    BookBorrow findTopByBookHoldIdOrderByCreatedAtDesc(Long bookHoldId);

    int countBybookHoldId(Long bookId);

    int countByUserIdAndStatus(Long userId, BorrowStatus status);

    @Query(value = """
            SELECT * FROM book_borrow
            WHERE user_id = :userId
              AND book_id = :bookId
              AND status IN :statuses
              AND is_deleted = false
            ORDER BY created_at DESC
            LIMIT 1
            """, nativeQuery = true)
    Optional<BookBorrow> findMyActiveBorrow(@Param("userId") Long userId,
                                            @Param("bookId") Long bookId,
                                            @Param("statuses") List<String> statuses);
}
