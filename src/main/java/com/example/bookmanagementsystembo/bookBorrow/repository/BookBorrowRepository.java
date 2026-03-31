package com.example.bookmanagementsystembo.bookBorrow.repository;

import com.example.bookmanagementsystembo.bookBorrow.entity.BookBorrow;
import com.example.bookmanagementsystembo.bookBorrow.enums.BorrowStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookBorrowRepository extends JpaRepository<BookBorrow, Long>, BookBorrowQueryRepository {
    BookBorrow findTopByBookHoldIdOrderByCreatedAtDesc(Long bookHoldId);

    int countBybookHoldId(Long bookId);

    int countByUserIdAndStatus(Long userId, BorrowStatus status);

    /** 주어진 상태의 전체 대출 건수를 반환합니다. */
    long countByStatus(BorrowStatus status);

    /**
     * D-1 알림 대상 조회: 특정 상태이면서 반납 예정일이 [from, to) 범위인 대출 목록을 반환합니다.
     * to는 exclusive로 처리하여 밀리초 단위 경계값 누락을 방지합니다.
     */
    @Query("SELECT b FROM BookBorrow b WHERE b.status = :status AND b.dueDate >= :from AND b.dueDate < :to")
    List<BookBorrow> findDueSoonTargets(@Param("status") BorrowStatus status,
                                        @Param("from") LocalDateTime from,
                                        @Param("to") LocalDateTime to);

    /** 특정 상태이면서 반납 예정일이 기준 일시 이전인 대출 목록을 반환합니다. (연체 처리용) */
    List<BookBorrow> findAllByStatusAndDueDateBefore(BorrowStatus status, LocalDateTime before);

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
