package com.example.bookmanagementsystembo.kakaobook.service;

import com.example.bookmanagementsystembo.exception.CoreException;
import com.example.bookmanagementsystembo.exception.ErrorType;
import com.example.bookmanagementsystembo.kakaobook.dto.KakaoBookSearchParams;
import com.example.bookmanagementsystembo.kakaobook.dto.KakaoBookSearchRes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
@Slf4j
public class KakaoBookSearchService {

    private static final String KAKAO_BOOK_API_URL = "https://dapi.kakao.com/v3/search/book";

    @Value("${kakao.api.key}")
    private String REST_API_KEY;

    private final RestClient restClient;

    /**
     * 카카오 도서 검색 API 프록시.
     * query 필수, 나머지 파라미터는 null이면 카카오 API 기본값 적용.
     */
    public KakaoBookSearchRes search(KakaoBookSearchParams params) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(KAKAO_BOOK_API_URL)
                .queryParam("query", params.query());

        if (StringUtils.hasText(params.sort()))   builder.queryParam("sort",   params.sort());
        if (params.page()   != null)              builder.queryParam("page",   params.page());
        if (params.size()   != null)              builder.queryParam("size",   params.size());
        if (StringUtils.hasText(params.target())) builder.queryParam("target", params.target());

        URI uri = builder.build().encode(StandardCharsets.UTF_8).toUri();

        return callKakaoApi(uri, params.query());
    }

    /** 기존 제목 전용 검색 (하위 호환) */
    public KakaoBookSearchRes getBooksByTitle(String title) {
        URI uri = UriComponentsBuilder.fromHttpUrl(KAKAO_BOOK_API_URL)
                .queryParam("target", "title")
                .queryParam("query", title)
                .build()
                .encode(StandardCharsets.UTF_8)
                .toUri();
        return callKakaoApi(uri, title);
    }

    private KakaoBookSearchRes callKakaoApi(URI uri, String queryForLog) {
        return restClient.get()
                .uri(uri)
                .header(HttpHeaders.AUTHORIZATION, REST_API_KEY)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    log.error("[KakaoBookSearch] 외부 API 오류 query={} status={}", queryForLog, res.getStatusCode());
                    throw new CoreException(ErrorType.BOOK_EXTERNAL_SERVICE_ERROR, queryForLog);
                })
                .body(KakaoBookSearchRes.class);
    }
}
