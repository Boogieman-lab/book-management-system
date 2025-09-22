package com.example.bookmanagementsystembo.book.domain.dto;

import java.util.List;

public record KakaoBookSearchDto(List<KakaoBookDocumentDto> documents, KakaoBookMetaDto meta) {
}
