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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
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
                    final String isbnApiValue = node.path("isbn").asText();
                    final String isbnToSave = Arrays.stream(isbnApiValue.split(" "))
                            .filter(isbn -> isbn.length() == 13)
                            .findFirst()
                            .orElse(null);

                    if (isbnToSave == null) {
                        continue;
                    }

                    Book book = bookRepository.findByIsbn(isbnToSave)
                            .orElseGet(() -> {
                                Book newBook = Book.builder()
                                        .title(node.path("title").asText())
                                        .publisher(node.path("publisher").asText())
                                        .isbn(isbnToSave)
                                        .imageUrl(node.path("thumbnail").asText())
                                        .build();

                                List<String> authorsList = objectMapper.convertValue(node.path("authors"), new TypeReference<List<String>>() {});
                                newBook.setAuthorsFromList(authorsList, objectMapper);

                                String datetime = node.path("datetime").asText();
                                if (datetime != null && !datetime.isEmpty()) {
                                    newBook.setPubDate(LocalDate.parse(datetime.substring(0, 10)));
                                }

                                return bookRepository.save(newBook);
                            });
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