package com.example.bookmanagementsystembo.reservation.scheduler;

import com.example.bookmanagementsystembo.notification.enums.NotificationType;
import com.example.bookmanagementsystembo.notification.service.NotificationService;
import com.example.bookmanagementsystembo.reservation.domain.entity.Reservation;
import com.example.bookmanagementsystembo.reservation.domain.service.ReservationBatchService;
import com.example.bookmanagementsystembo.reservation.infra.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 예약 관리 자동화 스케줄러.
 * 매일 00:00에 실행되어 픽업 기한이 만료된 예약을 취소하고 다음 대기자에게 승계합니다.
 * <p>
 * 처리 순서:
 * <ol>
 *   <li>만료 처리: NOTIFIED 예약 → EXPIRED 일괄 변경 (트랜잭션 커밋)</li>
 *   <li>만료 사용자 알림 발송 (격리된 try-catch)</li>
 *   <li>다음 대기자 승계 + 알림 발송 (건별 트랜잭션 + 격리된 try-catch)</li>
 * </ol>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationScheduler {

    private final ReservationBatchService reservationBatchService;
    private final ReservationRepository reservationRepository;
    private final NotificationService notificationService;

    /**
     * 매일 00:00에 만료된 예약을 처리합니다. (4일 보관 정책 자동화)
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void processExpiredReservations() {
        LocalDateTime now = LocalDateTime.now();
        log.info("[ReservationScheduler] 예약 만료 체크 시작 - 기준 일시: {}", now);

        // 1단계: 만료된 예약(NOTIFIED 상태 & 기한 초과)을 EXPIRED로 일괄 변경
        List<Reservation> expiredList = reservationBatchService.markExpiredReservations(now);
        log.info("[ReservationScheduler] {}건의 예약이 만료되었습니다.", expiredList.size());

        if (expiredList.isEmpty()) {
            return;
        }

        // 단일 쿼리로 bookHoldId → 도서 제목 일괄 조회 (N+1 방지)
        List<Long> bookHoldIds = expiredList.stream().map(Reservation::getBookHoldId).toList();
        Map<Long, String> bookTitleMap = reservationRepository.findBookTitlesByBookHoldIds(bookHoldIds);

        for (Reservation expired : expiredList) {
            Long bookHoldId = expired.getBookHoldId();
            String bookTitle = bookTitleMap.getOrDefault(bookHoldId, "알 수 없는 도서");

            // 2단계: 만료된 사용자에게 알림 발송 (장애 격리)
            try {
                notificationService.saveAndSend(
                        expired.getUserId(),
                        NotificationType.RESERVATION_EXPIRED,
                        "예약 도서 픽업 기한이 만료되어 예약이 취소되었습니다: " + bookTitle,
                        expired.getReservationId()
                );
            } catch (Exception e) {
                log.error("[ReservationScheduler] 만료 알림 발송 실패 — reservationId={}, userId={}",
                        expired.getReservationId(), expired.getUserId(), e);
            }

            // 3단계: 다음 대기자 승계 처리 (상태 변경 + 알림 각각 격리)
            try {
                reservationBatchService.promoteNextWaiting(bookHoldId)
                        .ifPresent(nextUserId -> {
                            log.info("[ReservationScheduler] 승계 완료 — bookHoldId={}, nextUserId={}", bookHoldId, nextUserId);
                            try {
                                notificationService.saveAndSend(
                                        nextUserId,
                                        NotificationType.RESERVATION_ARRIVED,
                                        "예약 대기 중인 도서를 수령할 수 있습니다. 4일 이내에 수령해주세요: " + bookTitle,
                                        bookHoldId
                                );
                            } catch (Exception e) {
                                log.error("[ReservationScheduler] 승계 알림 발송 실패 — bookHoldId={}, nextUserId={}",
                                        bookHoldId, nextUserId, e);
                            }
                        });
            } catch (Exception e) {
                log.error("[ReservationScheduler] 승계 처리 실패 — bookHoldId={}", bookHoldId, e);
            }
        }

        log.info("[ReservationScheduler] 예약 만료 체크 종료");
    }
}
