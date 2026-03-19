package com.example.bookmanagementsystembo.book.dto;

import com.example.bookmanagementsystembo.book.entity.Book;
import com.example.bookmanagementsystembo.book.utils.JsonUtils;

import java.time.LocalDateTime;
import java.util.List;

public record BookResponse(
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

    public static BookResponse from(Book entity) {
        return new BookResponse(
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
//    public static BookResponse fromKakao(KakaoBookDocumentDto dto) {
//        return new BookResponse(
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
    public static List<BookResponse> from(List<Book> dtos) {
        return dtos.stream()
                .map(BookResponse::from)
                .toList();
    }
//
//    public static List<BookResponse> fromKakao(List<KakaoBookDocumentDto> dtos) {
//        return dtos.stream().map(BookResponse::fromKakao).toList();
//    }

}
