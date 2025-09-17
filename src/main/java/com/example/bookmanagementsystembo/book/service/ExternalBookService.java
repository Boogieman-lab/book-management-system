package com.example.bookmanagementsystembo.book.service;

import com.example.bookmanagementsystembo.book.dto.ExternalBookDto;
import com.example.bookmanagementsystembo.exception.CoreException;
import com.example.bookmanagementsystembo.exception.ErrorType;
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
            try {
                JsonNode rootNode = new ObjectMapper().readTree(response.getBody());
                JsonNode documentsNode = rootNode.path("documents");

                books = StreamSupport.stream(documentsNode.spliterator(), false)
                        .map(node -> new ExternalBookDto(
                                jsonNodeToList(node.path("authors")),
                                node.path("contents").asText(),
                                node.path("datetime").asText(),
                                node.path("isbn").asText(),
                                node.path("price").asInt(-1),
                                node.path("publisher").asText(),
                                node.path("sale_price").asInt(-1),
                                node.path("status").asText(),
                                node.path("thumbnail").asText(),
                                node.path("title").asText(),
                                jsonNodeToList(node.path("translators")),
                                node.path("url").asText()
                        ))
                        .collect(Collectors.toList());
            } catch (IOException e) {
                throw new CoreException(ErrorType.EXTERNAL_BOOK_SERVICE_ERROR, title);
            }
        }
        return books;
    }

    /**
     * JsonNode 배열을 Stream API로 List<String> 변환
     */
    private List<String> jsonNodeToList(JsonNode arrayNode) {
        return StreamSupport.stream(arrayNode.spliterator(), false)
                .map(JsonNode::asText)
                .collect(Collectors.toList());
    }
}