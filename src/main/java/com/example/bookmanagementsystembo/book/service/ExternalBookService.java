package com.example.bookmanagementsystembo.book.service;

import com.example.bookmanagementsystembo.book.dto.ExternalBookDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExternalBookService {

    private final String API_URL = "https://dapi.kakao.com/v3/search/book";

    @Value("${kakao.api.key}")
    private String REST_API_KEY;

    public List<ExternalBookDto> getBooksByTitle(String title) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "KakaoAK " + REST_API_KEY);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        String url = API_URL + "?target=title&query=" + title;

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        List<ExternalBookDto> books = new ArrayList<>();

        if (response.getStatusCode() == HttpStatus.OK) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                JsonNode rootNode = objectMapper.readTree(response.getBody());
                JsonNode documentsNode = rootNode.path("documents");

                books = StreamSupport.stream(documentsNode.spliterator(), false)
                        .map(node -> {
                            List<String> authors = objectMapper.convertValue(
                                    node.path("authors"),
                                    new TypeReference<List<String>>() {}
                            );

                            List<String> translators = objectMapper.convertValue(
                                    node.path("translators"),
                                    new TypeReference<List<String>>() {}
                            );

                            return new ExternalBookDto(
                                    authors,
                                    node.path("contents").asText(),
                                    node.path("datetime").asText(),
                                    node.path("isbn").asText(),
                                    node.path("price").asInt(-1),
                                    node.path("publisher").asText(),
                                    node.path("sale_price").asInt(-1),
                                    node.path("status").asText(),
                                    node.path("thumbnail").asText(),
                                    node.path("title").asText(),
                                    translators,
                                    node.path("url").asText()
                            );
                        })
                        .collect(Collectors.toList());
            } catch (IOException e) {
                log.error("책 검색 실패", e);
            }
        }

        return books;
    }
}
