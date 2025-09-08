package com.example.bookmanagementsystembo.book.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.example.bookmanagementsystembo.book.entity.Book;
import com.example.bookmanagementsystembo.book.infra.BookRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import jakarta.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    private final String API_URL = "https://dapi.kakao.com/v3/search/book";
    private final String REST_API_KEY = "REST_API_KEY";

    /**
     * 제목으로 도서 검색 후 반환
     */
    @Transactional
    public List<Book> getBooksByTitle(String query) {
        // 1. API 호출
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "KakaoAK " + REST_API_KEY);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        String url = API_URL + "?target=title&query=" + query;

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                JsonNode rootNode = objectMapper.readTree(response.getBody());
                JsonNode documentsNode = rootNode.path("documents");

                List<Book> books = new ArrayList<>();
                for (JsonNode node : documentsNode) {
                    Book book = Book.builder()
                            .title(node.path("title").asText())
                            .publisher(node.path("publisher").asText())
                            .isbn(node.path("isbn").asText())
                            .imageUrl(node.path("thumbnail").asText())
                            .build();
                    // authors는 List → JSON 문자열로 변환
                    List<String> authorsList = objectMapper.convertValue(node.path("authors"), new TypeReference<List<String>>() {});
                    book.setAuthorsFromList(authorsList, objectMapper);

                    books.add(book);
                }
                return books;
            } catch (IOException e) {
                log.error("책 검색 실패", e);
            }
        }
        return Collections.emptyList();
    }
}
