package com.example.bookmanagementsystembo.book.dto;

import com.example.bookmanagementsystembo.book.entity.Book;
import com.example.bookmanagementsystembo.book.utils.JsonUtils;

import java.time.LocalDateTime;
import java.util.List;

public record BookRes(
        Long bookId,
        String title,
        String contents,
        String url,
        String isbn,
        LocalDateTime publishedAt,
        List<String> authors,
        List<String> translators,
        String publisher,
        int price,
        int salePrice,
        String thumbnail
) {

    public static BookRes from(Book entity) {
        return new BookRes(
                entity.getBookId(),
                entity.getTitle(),
                entity.getContents(),
                entity.getUrl(),
                entity.getIsbn(),
                entity.getPublishedAt(),
                JsonUtils.toList(entity.getAuthors()),
                JsonUtils.toList(entity.getTranslators()),
                entity.getPublisher(),
                entity.getPrice(),
                entity.getSalePrice(),
                entity.getThumbnail()
        );
    }

    //
//    public static BookRes fromKakao(KakaoBookDocumentDto dto) {
//        return new BookRes(
//                dto.title(),
//                dto.authors(),
//                dto.translators(),
//                dto.contents(),
//                dto.datetime(),
//                dto.isbn(),
//                dto.price(),
//                dto.publisher(),
//                dto.salePrice(),
//                dto.status(),
//                dto.thumbnail(),
//                dto.url()
//        );
//    }
    // 리스트 변환
    public static List<BookRes> from(List<Book> dtos) {
        return dtos.stream()
                .map(BookRes::from)
                .toList();
    }
//
//    public static List<BookRes> fromKakao(List<KakaoBookDocumentDto> dtos) {
//        return dtos.stream().map(BookRes::fromKakao).toList();
//    }

}
