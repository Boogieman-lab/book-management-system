package com.example.bookmanagementsystembo.book.domain.service;

import com.example.bookmanagementsystembo.book.domain.dto.KakaoBookSearchDto;
import com.example.bookmanagementsystembo.exception.CoreException;
import com.example.bookmanagementsystembo.exception.ErrorType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookSearchService {

    @Value("${kakao.api.key}")
    private String REST_API_KEY;

    public KakaoBookSearchDto getBooksByTitle(String title) {

        String API_URL = "https://dapi.kakao.com/v3/search/book";
        URI uri = UriComponentsBuilder.fromHttpUrl(API_URL)
                .queryParam("target", "title")
                .queryParam("query", title)
                .build()
                .encode(StandardCharsets.UTF_8)
                .toUri();

        return RestClient.create()
                .get()
                .uri(uri)
                .header(HttpHeaders.AUTHORIZATION, REST_API_KEY)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    throw new CoreException(ErrorType.BOOK_EXTERNAL_SERVICE_ERROR, title);
                })
                .body(KakaoBookSearchDto.class);
    }
}