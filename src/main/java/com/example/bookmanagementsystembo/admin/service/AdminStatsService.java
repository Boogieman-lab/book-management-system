package com.example.bookmanagementsystembo.admin.service;

import com.example.bookmanagementsystembo.admin.dto.AdminStatsOverviewResponse;
import com.example.bookmanagementsystembo.book.repository.BookRepository;
import com.example.bookmanagementsystembo.bookBorrow.enums.BorrowStatus;
import com.example.bookmanagementsystembo.bookBorrow.repository.BookBorrowRepository;
import com.example.bookmanagementsystembo.bookRequest.enums.BookRequestStatus;
import com.example.bookmanagementsystembo.bookRequest.repository.BookRequestRepository;
import com.example.bookmanagementsystembo.reservation.enums.ReservationStatus;
import com.example.bookmanagementsystembo.reservation.infra.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 관리자 대시보드 통계 서비스.
 * 각 도메인 Repository를 집계하여 통계 현황을 반환합니다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminStatsService {

    private final BookRepository bookRepository;
    private final BookBorrowRepository bookBorrowRepository;
    private final ReservationRepository reservationRepository;
    private final BookRequestRepository bookRequestRepository;

    /**
     * 대시보드 통계 개요를 조회합니다.
     * - 현재 대출 건수, 연체 건수, 예약 대기 건수, 승인 대기 신청 건수
     */
    public AdminStatsOverviewResponse getOverview() {
        return new AdminStatsOverviewResponse(
                bookBorrowRepository.countByStatus(BorrowStatus.BORROWED),
                bookBorrowRepository.countByStatus(BorrowStatus.OVERDUE),
                reservationRepository.countByStatus(ReservationStatus.WAITING),
                bookRequestRepository.countByStatus(BookRequestStatus.PENDING)
        );
    }
}
