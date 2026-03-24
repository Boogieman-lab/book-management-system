package com.example.bookmanagementsystembo.aladinbook.service;

import com.example.bookmanagementsystembo.aladinbook.dto.*;
import com.example.bookmanagementsystembo.exception.CoreException;
import com.example.bookmanagementsystembo.exception.ErrorType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * 알라딘 ItemSearch API 프록시 서비스.
 *
 * <ul>
 *   <li>표지 이미지: /coversum/ → /cover500/ 치환으로 고해상도 제공</li>
 *   <li>ISBN: isbn13 (13자리) 우선 사용, 없으면 isbn (10자리) 폴백</li>
 *   <li>저자: "홍길동 (지은이), 이순신 (옮긴이)" 형식에서 역할 제거 후 리스트로 반환</li>
 *   <li>응답 형식: 프론트엔드 호환을 위해 documents + meta(is_end) 구조로 정규화</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AladinBookSearchService {

    private static final String ALADIN_SEARCH_URL = "http://www.aladin.co.kr/ttb/api/ItemSearch.aspx";

    @Value("${aladin.api.key}")
    private String ttbKey;

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public AladinBookSearchResponse search(AladinBookSearchParams params) {
        int start      = params.start()      != null ? params.start()                      : 1;
        int maxResults = params.maxResults() != null ? Math.min(params.maxResults(), 50)   : 10;

        URI uri = UriComponentsBuilder.fromHttpUrl(ALADIN_SEARCH_URL)
                .queryParam("ttbkey",       ttbKey)
                .queryParam("Query",        params.query())
                .queryParam("QueryType",    resolveQueryType(params.queryType()))
                .queryParam("SearchTarget", "Book")
                .queryParam("Start",        start)
                .queryParam("MaxResults",   maxResults)
                .queryParam("Cover",        "Big")
                .queryParam("Output",       "JS")
                .queryParam("Version",      "20131101")
                .build()
                .encode(StandardCharsets.UTF_8)
                .toUri();

        String rawBody = restClient.get()
                .uri(uri)
                .accept(MediaType.ALL)
                .retrieve()
                .onStatus(org.springframework.http.HttpStatusCode::isError, (req, res) -> {
                    log.error("[AladinBookSearch] 외부 API 오류 query={} status={}", params.query(), res.getStatusCode());
                    throw new CoreException(ErrorType.BOOK_EXTERNAL_SERVICE_ERROR, params.query());
                })
                .body(String.class);

        AladinSearchResponse raw = parseResponse(rawBody, params.query());
        return normalize(raw, start, maxResults);
    }

    private AladinSearchResponse parseResponse(String body, String query) {
        try {
            return objectMapper.readValue(body, AladinSearchResponse.class);
        } catch (Exception e) {
            log.error("[AladinBookSearch] 응답 파싱 오류 query={} error={}", query, e.getMessage());
            throw new CoreException(ErrorType.BOOK_EXTERNAL_SERVICE_ERROR, query);
        }
    }

    private AladinBookSearchResponse normalize(AladinSearchResponse raw, int start, int maxResults) {
        if (raw == null || raw.items() == null) {
            return new AladinBookSearchResponse(List.of(), new AladinBookMeta(true, 0));
        }

        List<AladinBookDocument> docs = raw.items().stream()
                .map(this::toDocument)
                .toList();

        boolean isEnd = (long) start * maxResults >= raw.totalResults();
        return new AladinBookSearchResponse(docs, new AladinBookMeta(isEnd, raw.totalResults()));
    }

    private AladinBookDocument toDocument(AladinItemDto item) {
        return new AladinBookDocument(
                item.title(),
                parseAuthors(item.author()),
                item.publisher(),
                item.pubdate(),
                enhanceCover(item.cover()),
                normalizeIsbn(item.isbn13(), item.isbn()),
                item.description(),
                item.categoryName()
        );
    }

    /**
     * /coversum/ → /cover500/ 치환으로 고해상도 표지 이미지 URL 반환.
     */
    private String enhanceCover(String coverUrl) {
        if (coverUrl == null) return null;
        return coverUrl.replace("/coversum/", "/cover500/");
    }

    /**
     * isbn13(13자리) 우선, 비어있으면 isbn(10자리) 폴백.
     */
    private String normalizeIsbn(String isbn13, String isbn) {
        if (isbn13 != null && !isbn13.isBlank()) return isbn13;
        return isbn;
    }

    /**
     * "홍길동 (지은이), 이순신 (옮긴이)" → ["홍길동", "이순신"].
     * 쉼표로 분리 후 괄호 역할 표기를 제거합니다.
     */
    private List<String> parseAuthors(String authorStr) {
        if (authorStr == null || authorStr.isBlank()) return List.of();
        return Arrays.stream(authorStr.split(","))
                .map(s -> s.replaceAll("\\([^)]*\\)", "").trim())
                .filter(s -> !s.isEmpty())
                .toList();
    }

    private String resolveQueryType(String target) {
        if (target == null) return "Keyword";
        return switch (target.toLowerCase()) {
            case "title"     -> "Title";
            case "author"    -> "Author";
            case "publisher" -> "Publisher";
            default          -> "Keyword";
        };
    }
}
