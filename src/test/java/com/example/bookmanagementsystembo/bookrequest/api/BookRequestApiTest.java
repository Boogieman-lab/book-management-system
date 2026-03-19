package com.example.bookmanagementsystembo.bookRequest.api;

import com.example.bookmanagementsystembo.bookRequest.dto.BookRequestCreateRequest;
import com.example.bookmanagementsystembo.bookRequest.dto.BookRequestSummaryPageResponse;
import com.example.bookmanagementsystembo.bookRequest.dto.BookRequestSummaryResponse;
import com.example.bookmanagementsystembo.bookRequest.dto.BookRequestUpdateRequest;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

public class BookRequestApiTest {
    RestClient restClient = RestClient.create("http://localhost:8080");

    @Test
    void create() {
        Long userId = 1L;
        BookRequestCreateRequest createReq1 = new BookRequestCreateRequest(
                "자바의 정석",
                "남궁성",
                "도우출판",
                "979118665",
                "자바 공부용으로 신청합니다."
        );
        BookRequestCreateRequest createReq2 = new BookRequestCreateRequest(
                "JPA",
                "홍길동, 김영환",
                "도우출판",
                "979118615",
                "자바 공부용으로 신청합니다."
        );

        BookRequestSummaryResponse created1 = create(createReq1, userId);
        BookRequestSummaryResponse created2 = create(createReq2, userId);
        System.out.println("created1 = " + created1);
        System.out.println("created2 = " + created2);

        Long bookRequestId = created1.bookRequestId();

        BookRequestSummaryResponse read = read(bookRequestId);
        System.out.println("read = " + read);

        BookRequestSummaryPageResponse page = readAll();
        System.out.println("page = " + page);

        BookRequestUpdateRequest updateReq = new BookRequestUpdateRequest(
                "자바의 정석 수정",
                "남궁성",
                "도우출판",
                "979118665",
                "자바 공부용으로 신청 수정합니다."
        );

        BookRequestSummaryResponse updated = update(1L, updateReq);
        System.out.println("updated = " + updated);
    }


    BookRequestSummaryResponse create(BookRequestCreateRequest request, Long userId) {
        return restClient.post()
                .uri("/api/book-requests")
                .body(request)
                .retrieve()
                .body(BookRequestSummaryResponse.class);
    }

    BookRequestSummaryResponse read(Long bookRequestId) {
        return restClient.get()
                .uri("/api/book-requests/{bookRequestId}", bookRequestId)
                .retrieve()
                .body(BookRequestSummaryResponse.class);
    }

    BookRequestSummaryPageResponse readAll() {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/book-requests")
                        .queryParam("page", 1L)
                        .queryParam("pageSize", 10L)
                        .build())
                .retrieve()
                .body(BookRequestSummaryPageResponse.class);
    }

    BookRequestSummaryResponse update(Long bookRequestId, BookRequestUpdateRequest request) {
        return restClient.put()
                .uri("/api/book-requests/{bookRequestId}", bookRequestId)
                .body(request)
                .retrieve()
                .body(BookRequestSummaryResponse.class);
    }

}
