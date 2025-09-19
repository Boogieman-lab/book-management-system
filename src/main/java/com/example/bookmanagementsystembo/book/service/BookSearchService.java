package com.example.bookmanagementsystembo.book.service;

import com.example.bookmanagementsystembo.book.dto.KakaoBookSearchDto;
import com.example.bookmanagementsystembo.exception.CoreException;
import com.example.bookmanagementsystembo.exception.ErrorType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookSearchService {

    private final String API_URL = "https://dapi.kakao.com/v3/search/book";

    @Value("${kakao.api.key}")
    private String REST_API_KEY;

    public KakaoBookSearchDto getBooksByTitle(String title) {

        String uri = UriComponentsBuilder.fromHttpUrl(API_URL)
                .queryParam("target", "title")
                .queryParam("query", title)
                .build(true)
                .toUriString();

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, REST_API_KEY);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, requestEntity, String.class);

        if (!response.getStatusCode().equals(HttpStatus.OK)) {
            throw new CoreException(ErrorType.BOOK_EXTERNAL_SERVICE_ERROR, title);
        }
        KakaoBookSearchDto kakao;
        try {
            kakao = new ObjectMapper().readValue(response.getBody(), KakaoBookSearchDto.class);

        } catch (IOException e) {
            throw new CoreException(ErrorType.BOOK_EXTERNAL_JSON_PROCESSING_ERROR, title);
        }
        return kakao;
    }
}