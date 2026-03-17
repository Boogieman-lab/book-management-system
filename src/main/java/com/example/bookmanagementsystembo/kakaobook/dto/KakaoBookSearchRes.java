package com.example.bookmanagementsystembo.kakaobook.dto;


import java.util.List;

public record KakaoBookSearchRes(List<KakaoBookDocumentDto> documents, KakaoBookMetaDto metaInfo){
}
