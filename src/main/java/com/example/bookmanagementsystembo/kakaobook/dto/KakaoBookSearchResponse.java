package com.example.bookmanagementsystembo.kakaobook.dto;


import java.util.List;

public record KakaoBookSearchResponse(List<KakaoBookDocumentDto> documents, KakaoBookMetaDto metaInfo){
}
