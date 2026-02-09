package com.example.bookmanagementsystembo.bookrequest.api;

import com.example.bookmanagementsystembo.bookrequest.dto.BookRequestCreateReq;
import com.example.bookmanagementsystembo.bookrequest.dto.BookRequestPageRes;
import com.example.bookmanagementsystembo.bookrequest.dto.BookRequestRes;
import com.example.bookmanagementsystembo.bookrequest.dto.BookRequestUpdateReq;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

public class BookRequestApiTest {
    RestClient restClient = RestClient.create("http://localhost:8080");

    @Test
    void create() {
        Long userId = 1L;
        BookRequestCreateReq createReq1 = new BookRequestCreateReq(
                "자바의 정석",
                "남궁성",
                "도우출판",
                "979118665",
                "자바 공부용으로 신청합니다."
        );
        BookRequestCreateReq createReq2 = new BookRequestCreateReq(
                "JPA",
                "홍길동, 김영환",
                "도우출판",
                "979118615",
                "자바 공부용으로 신청합니다."
        );

        BookRequestRes created1 = create(createReq1, userId);
        BookRequestRes created2 = create(createReq2, userId);
        System.out.println("created1 = " + created1);
        System.out.println("created2 = " + created2);

        Long bookRequestId = created1.bookRequestId();

        BookRequestRes read = read(bookRequestId);
        System.out.println("read = " + read);

        BookRequestPageRes page = readAll(1L, 10L);
        System.out.println("page = " + page);

        BookRequestUpdateReq updateReq = new BookRequestUpdateReq(
                "자바의 정석 수정",
                "남궁성",
                "도우출판",
                "979118665",
                "자바 공부용으로 신청 수정합니다."
        );

        BookRequestRes updated = update(1L, updateReq);
        System.out.println("updated = " + updated);
    }


    BookRequestRes create(BookRequestCreateReq request, Long userId) {
        return restClient.post()
                .uri("/api/book-requests")
                .body(request)
                .retrieve()
                .body(BookRequestRes.class);
    }

    BookRequestRes read(Long bookRequestId) {
        return restClient.get()
                .uri("/api/book-requests/{bookRequestId}", bookRequestId)
                .retrieve()
                .body(BookRequestRes.class);
    }

    BookRequestPageRes readAll(Long page, Long pageSize) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/book-requests")
                        .queryParam("page", page)
                        .queryParam("pageSize", pageSize)
                        .build())
                .retrieve()
                .body(BookRequestPageRes.class);
    }

    BookRequestRes update(Long bookRequestId, BookRequestUpdateReq request) {
        return restClient.put()
                .uri("/api/book-requests/{bookRequestId}", bookRequestId)
                .body(request)
                .retrieve()
                .body(BookRequestRes.class);
    }

}
