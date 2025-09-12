package com.example.bookmanagementsystembo.book.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.example.bookmanagementsystembo.book.entity.Book;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class BookService {

    private final String API_URL = "https://dapi.kakao.com/v3/search/book";
    private final String REST_API_KEY = "REST_API_KEY";

    public List<Book> getBooksByTitle(String query) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "KakaoAK " + REST_API_KEY);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        String url = API_URL + "?target=title&query=" + query;

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        List<Book> books = new ArrayList<>();

        if (response.getStatusCode() == HttpStatus.OK) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                JsonNode rootNode = objectMapper.readTree(response.getBody());
                JsonNode documentsNode = rootNode.path("documents");

                for (JsonNode node : documentsNode) {
                    final String isbnApiValue = node.path("isbn").asText();
                    final String isbn = Arrays.stream(isbnApiValue.split(" "))
                            .filter(i -> i.length() == 13)
                            .findFirst()
                            .orElse(null);

                    if (isbn == null) {
                        continue;
                    }

                    Book book = Book.builder()
                            .title(node.path("title").asText())
                            .publisher(node.path("publisher").asText())
                            .isbn(isbn)
                            .imageUrl(node.path("thumbnail").asText())
                            .build();

                    List<String> authorsList = objectMapper.convertValue(node.path("authors"), new TypeReference<List<String>>() {});
                    book.setAuthorsFromList(authorsList, objectMapper);

                    String datetime = node.path("datetime").asText();
                    if (datetime != null && !datetime.isEmpty()) {
                        book.setPubDate(LocalDate.parse(datetime.substring(0, 10)));
                    }

                    books.add(book);
                }
            } catch (IOException e) {
                log.error("책 검색 실패", e);
            }
        }

        return books;
    }
}
