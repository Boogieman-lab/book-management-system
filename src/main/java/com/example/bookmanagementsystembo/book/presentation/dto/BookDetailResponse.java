package com.example.bookmanagementsystembo.book.presentation.dto;

import com.example.bookmanagementsystembo.book.domain.entity.Book;
import com.example.bookmanagementsystembo.bookBorrow.domain.entity.BookBorrow;
import com.example.bookmanagementsystembo.bookHold.domain.entity.BookHold;
import com.example.bookmanagementsystembo.reservation.domain.entity.Reservation;

import java.time.LocalDateTime;
import java.util.List;

public record BookDetailResponse(
        Long bookId,
        String title,
        String authors,
        List<String> translators,
        String contents,
        LocalDateTime datetime,
        String isbn,
        int price,
        String publisher,
        int salePrice,
        String status,
        String thumbnail,
        String url,
        List<HoldInfo> holds
) {

    public static BookDetailResponse from(Book book, String authors, List<HoldInfo> holds) {
        List<String> translators = book.getTranslators() != null ? List.of(book.getTranslators().split(",")) : List.of();

        return new BookDetailResponse(
                book.getBookId(),
                book.getTitle(),
                authors,
                translators,
                book.getContents(),
                book.getDatetime(),
                book.getIsbn(),
                book.getPrice(),
                book.getPublisher(),
                book.getSalePrice(),
                book.getStatus(),
                book.getThumbnail(),
                book.getUrl(),
                holds
        );
    }

    public record HoldInfo(
            Long holdId,
            Long loanUserId,
            String status,          // 기존 enum 이름
            String statusDesc,      // 화면에 보여줄 한글 상태
            String location,
            String borrowStatus,
            List<Long> reservationUserIds
    ) {

        public static HoldInfo from(BookHold hold, BookBorrow borrow, List<Reservation> reservations) {
            List<Long> reservationUserIds = reservations.stream()
                    .map(Reservation::getUserId)
                    .toList();

            return new HoldInfo(
                    hold.getBookHoldId(),
                    borrow != null ? borrow.getUserId() : null,
                    hold.getStatus().name(),
                    hold.getStatus().getDesc(),
                    hold.getLocation(),
                    borrow != null ? borrow.getStatus().name() : null,
                    reservationUserIds
            );
        }

    }
}
