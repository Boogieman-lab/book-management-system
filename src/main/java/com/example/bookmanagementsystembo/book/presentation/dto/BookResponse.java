package com.example.bookmanagementsystembo.book.presentation.dto;

import com.example.bookmanagementsystembo.book.domain.dto.BookDto;
import com.example.bookmanagementsystembo.book.domain.dto.KakaoBookDocumentDto;
import com.example.bookmanagementsystembo.bookBorrow.domain.entity.BookBorrow;
import com.example.bookmanagementsystembo.bookHold.domain.entity.BookHold;
import com.example.bookmanagementsystembo.reservation.domain.entity.Reservation;

import java.time.LocalDateTime;
import java.util.List;

public record BookResponse(
        String title,
        List<String> authors,
        List<String> translators,
        String contents,
        LocalDateTime datetime,
        String isbn,
        int price,
        String publisher,
        int salePrice,
        String status,
        String thumbnail,
        String url
) {

    public static BookResponse from(BookDto dto) {
        return new BookResponse(
                dto.title(),
                dto.authors(),
                dto.translators(),
                dto.contents(),
                dto.datetime(),
                dto.isbn(),
                dto.price(),
                dto.publisher(),
                dto.salePrice(),
                dto.status(),
                dto.thumbnail(),
                dto.url()
        );
    }


    public static BookResponse fromKakao(KakaoBookDocumentDto dto) {
        return new BookResponse(
                dto.title(),
                dto.authors(),
                dto.translators(),
                dto.contents(),
                dto.datetime(),
                dto.isbn(),
                dto.price(),
                dto.publisher(),
                dto.salePrice(),
                dto.status(),
                dto.thumbnail(),
                dto.url()
        );
    }
    // 리스트 변환
    public static List<BookResponse> from(List<BookDto> dtos) {
        return dtos.stream()
                .map(BookResponse::from)
                .toList();
    }

    public static List<BookResponse> fromKakao(List<KakaoBookDocumentDto> dtos) {
        return dtos.stream().map(BookResponse::fromKakao).toList();
    }

    public record HoldInfo(
            Long holdId,
            String status,
            String location,
            String borrowStatus,
            String reservationStatus
    ) {
        public static HoldInfo from(BookHold hold, BookBorrow borrow, Reservation reservation) {
            return new HoldInfo(
                    hold.getBookHoldId(),
                    hold.getStatus().name(),
                    hold.getLocation(),
                    borrow != null ? borrow.getStatus().name() : null,
                    reservation != null ? reservation.getStatus() : null
            );
        }
    }

}
