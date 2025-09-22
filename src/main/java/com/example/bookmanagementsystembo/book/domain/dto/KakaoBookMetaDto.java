package com.example.bookmanagementsystembo.book.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KakaoBookMetaDto(@JsonProperty("is_end") boolean isEnd,
                               @JsonProperty("pageable_count") int pageableCount,
                               @JsonProperty("total_count") int totalCount) {
}
