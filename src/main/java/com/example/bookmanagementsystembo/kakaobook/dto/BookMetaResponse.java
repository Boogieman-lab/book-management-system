package com.example.bookmanagementsystembo.kakaobook.dto;

public record BookMetaResponse(boolean isEnd, int pageableCount, int totalCount) {
    public static BookMetaResponse fromKakao(KakaoBookMetaDto dto) {
        return new BookMetaResponse(dto.isEnd(), dto.pageableCount(), dto.totalCount());
    }
}
