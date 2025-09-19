package com.example.bookmanagementsystembo.book.presentation.dto;

import com.example.bookmanagementsystembo.book.dto.KakaoBookMetaDto;

public record BookMetaResponse(boolean isEnd, int pageableCount, int totalCount) {
    public static BookMetaResponse fromKakao(KakaoBookMetaDto dto) {
        return new BookMetaResponse(dto.isEnd(), dto.pageableCount(), dto.totalCount());
    }
}
