package com.example.bookmanagementsystembo.book.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record KakaoBookDocumentDto(List<String> authors,
                                   String contents,
                                   String datetime,
                                   String isbn,
                                   int price,
                                   String publisher,
                                   @JsonProperty("sale_price") int salePrice,
                                   String status,
                                   String thumbnail,
                                   String title,
                                   List<String> translators,
                                   String url) {
}
