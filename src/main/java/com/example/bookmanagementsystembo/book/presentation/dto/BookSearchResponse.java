package com.example.bookmanagementsystembo.book.presentation.dto;


import com.example.bookmanagementsystembo.book.dto.KakaoBookDocumentDto;
import com.example.bookmanagementsystembo.book.dto.KakaoBookMetaDto;

import java.util.List;

public record BookSearchResponse(List<BookResponse> books, BookMetaResponse metaInfo){
    public static BookSearchResponse from(List<KakaoBookDocumentDto> documents, KakaoBookMetaDto meta) {
        return new BookSearchResponse(BookResponse.fromKakao(documents), BookMetaResponse.fromKakao(meta));
    }
}
