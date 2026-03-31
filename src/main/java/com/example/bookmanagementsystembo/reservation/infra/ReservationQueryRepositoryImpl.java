package com.example.bookmanagementsystembo.reservation.infra;

import com.example.bookmanagementsystembo.book.entity.QBook;
import com.example.bookmanagementsystembo.bookHold.entity.QBookHold;
import com.example.bookmanagementsystembo.reservation.domain.entity.QReservation;
import com.example.bookmanagementsystembo.reservation.domain.entity.Reservation;
import com.example.bookmanagementsystembo.reservation.enums.ReservationStatus;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ReservationQueryRepositoryImpl implements ReservationQueryRepository {

    private final JPAQueryFactory qf;

    private static final QReservation reservation = QReservation.reservation;
    private static final QBookHold bookHold = QBookHold.bookHold;
    private static final QBook book = QBook.book;

    /**
     * bookId 기준 예약 목록을 조회합니다.
     * Reservation.bookHoldId ↔ BookHold.bookHoldId JOIN으로 bookId 필터링합니다.
     */
    @Override
    public List<Reservation> findByBookIdAndStatus(Long bookId, ReservationStatus status) {
        return qf.selectFrom(reservation)
                .join(bookHold).on(reservation.bookHoldId.eq(bookHold.bookHoldId))
                .where(
                        bookHold.bookId.eq(bookId),
                        reservation.status.eq(status)
                )
                .orderBy(reservation.reservedAt.asc())
                .fetch();
    }

    /**
     * bookHoldId 목록에 대해 bookHoldId → 도서 제목 맵을 단일 쿼리로 반환합니다.
     * book_hold → book JOIN으로 N+1 없이 일괄 조회합니다.
     */
    @Override
    public Map<Long, String> findBookTitlesByBookHoldIds(List<Long> bookHoldIds) {
        return qf.select(bookHold.bookHoldId, book.title)
                .from(bookHold)
                .join(book).on(bookHold.bookId.eq(book.bookId))
                .where(bookHold.bookHoldId.in(bookHoldIds))
                .fetch()
                .stream()
                .collect(Collectors.toMap(
                        t -> t.get(bookHold.bookHoldId),
                        t -> t.get(book.title)
                ));
    }
}
