package com.example.bookmanagementsystembo.bookBorrow.scheduler;

import com.example.bookmanagementsystembo.bookBorrow.entity.BookBorrow;
import com.example.bookmanagementsystembo.bookBorrow.enums.BorrowStatus;
import com.example.bookmanagementsystembo.bookBorrow.repository.BookBorrowRepository;
import com.example.bookmanagementsystembo.bookBorrow.service.BookBorrowBatchService;
import com.example.bookmanagementsystembo.notification.enums.NotificationType;
import com.example.bookmanagementsystembo.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 반납 예정일 알림 배치 스케줄러.
 * <p>
 * 매일 오전 6시에 순서대로 실행됩니다:
 * <ol>
 *   <li>06:00 — 연체 처리: 반납 기한 초과 건의 상태를 {@code OVERDUE}로 변경 후 알림 발송</li>
 *   <li>06:05 — D-1 알림: 내일 반납 예정인 건에 {@code BORROW_DUE_SOON} 알림 발송</li>
 * </ol>
 * 상태 변경(@Transactional)과 알림 발송은 트랜잭션이 분리되어,
 * 알림 시스템 장애가 DB 상태 변경 롤백을 유발하지 않습니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DueDateNotificationScheduler {

    private final BookBorrowRepository bookBorrowRepository;
    private final BookBorrowBatchService bookBorrowBatchService;
    private final NotificationService notificationService;

    /**
     * 연체 처리 배치. 매일 06:00 실행.
     * <p>
     * 1단계: {@link BookBorrowBatchService#markOverdueBorrows} 트랜잭션 커밋으로 상태 변경 확정.
     * 2단계: 각 건별 알림 발송 — 단건 실패가 나머지 처리에 영향을 주지 않도록 격리합니다.
     */
    @Scheduled(cron = "0 0 6 * * *")
    public void processOverdueBorrows() {
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();

        List<BookBorrow> overdueList = bookBorrowBatchService.markOverdueBorrows(todayStart);
        log.info("[DueDateScheduler] 연체 처리: {}건 상태 변경 완료", overdueList.size());

        for (BookBorrow borrow : overdueList) {
            try {
                notificationService.saveAndSend(
                        borrow.getUserId(),
                        NotificationType.BORROW_OVERDUE,
                        "반납 기한이 초과된 도서가 있습니다. 즉시 반납해 주세요.",
                        borrow.getBookBorrowId()
                );
            } catch (Exception e) {
                log.error("[DueDateScheduler] 연체 알림 발송 실패 — borrowId={}", borrow.getBookBorrowId(), e);
            }
        }
    }

    /**
     * D-1 알림 배치. 매일 06:05 실행 (연체 처리 완료 후 실행되도록 5분 후에 배치).
     * <p>
     * 내일 반납 예정인 대출 건에 알림을 발송합니다.
     * 단건 실패가 나머지 발송에 영향을 주지 않도록 격리합니다.
     */
    @Scheduled(cron = "0 5 6 * * *")
    public void sendDueSoonNotifications() {
        LocalDateTime tomorrowStart = LocalDate.now().plusDays(1).atStartOfDay();
        LocalDateTime dayAfterTomorrowStart = tomorrowStart.plusDays(1); // exclusive 상한

        List<BookBorrow> targets = bookBorrowRepository
                .findDueSoonTargets(BorrowStatus.BORROWED, tomorrowStart, dayAfterTomorrowStart);

        log.info("[DueDateScheduler] D-1 알림 대상: {}건", targets.size());

        for (BookBorrow borrow : targets) {
            try {
                if (notificationService.alreadySentToday(NotificationType.BORROW_DUE_SOON, borrow.getBookBorrowId())) {
                    log.debug("[DueDateScheduler] D-1 알림 중복 건너뜀 — borrowId={}", borrow.getBookBorrowId());
                    continue;
                }
                notificationService.saveAndSend(
                        borrow.getUserId(),
                        NotificationType.BORROW_DUE_SOON,
                        "내일까지 반납 예정인 도서가 있습니다. 기한 내 반납해 주세요.",
                        borrow.getBookBorrowId()
                );
            } catch (Exception e) {
                log.error("[DueDateScheduler] D-1 알림 발송 실패 — borrowId={}", borrow.getBookBorrowId(), e);
            }
        }
    }
}
