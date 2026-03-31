package com.example.bookmanagementsystembo.bookBorrow.service;

import com.example.bookmanagementsystembo.bookBorrow.entity.BookBorrow;
import com.example.bookmanagementsystembo.bookBorrow.enums.BorrowStatus;
import com.example.bookmanagementsystembo.bookBorrow.repository.BookBorrowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 대출 배치 처리 서비스.
 * 스케줄러에서 호출하며, 상태 변경 트랜잭션을 알림 발송과 분리하여
 * 외부 시스템(알림) 장애가 DB 롤백을 유발하지 않도록 격리합니다.
 */
@Service
@RequiredArgsConstructor
public class BookBorrowBatchService {

    private final BookBorrowRepository bookBorrowRepository;

    /**
     * 반납 기한이 지난 대출 건의 상태를 OVERDUE로 일괄 변경하고 변경된 목록을 반환합니다.
     * 이 메서드의 트랜잭션이 커밋되면 상태 변경이 확정됩니다.
     * 이후 호출 측에서 알림 발송을 별도로 수행하세요.
     *
     * @param todayStart 오늘 00:00:00 (이 시각 이전의 dueDate를 연체로 처리)
     * @return 상태가 OVERDUE로 변경된 대출 목록
     */
    @Transactional
    public List<BookBorrow> markOverdueBorrows(LocalDateTime todayStart) {
        List<BookBorrow> targets = bookBorrowRepository
                .findAllByStatusAndDueDateBefore(BorrowStatus.BORROWED, todayStart);
        targets.forEach(b -> b.updateStatus(BorrowStatus.OVERDUE));
        return targets;
    }
}
