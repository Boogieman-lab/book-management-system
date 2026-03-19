package com.example.bookmanagementsystembo.bookBorrow.api;

import com.example.bookmanagementsystembo.bookBorrow.dto.BorrowCreateReq;
import com.example.bookmanagementsystembo.bookBorrow.dto.BorrowPageRes;
import com.example.bookmanagementsystembo.bookBorrow.dto.BorrowRes;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

/**
 * 대출/반납 API 통합 테스트 (수동 실행용).
 * 서버가 localhost:8080에서 실행 중이어야 합니다.
 * JWT 토큰은 실제 인증 후 Authorization 헤더에 설정해야 합니다.
 */
@Disabled("수동 실행용 - 서버 기동 후 직접 실행")
public class BorrowApiIntegrationTest {

    private static final String BASE_URL = "http://localhost:8080";
    private static final String AUTH_TOKEN = "Bearer <JWT_TOKEN_HERE>";

    RestClient restClient = RestClient.builder()
            .baseUrl(BASE_URL)
            .defaultHeader("Authorization", AUTH_TOKEN)
            .build();

    @Test
    void borrowAndReturn() {
        // 1. 대출
        BorrowCreateReq borrowReq = new BorrowCreateReq(1L, "학습용 대출");
        BorrowRes borrowRes = restClient.post()
                .uri("/api/v1/borrows")
                .body(borrowReq)
                .retrieve()
                .body(BorrowRes.class);
        System.out.println("borrowRes = " + borrowRes);

        // 2. 반납
        assert borrowRes != null;
        restClient.post()
                .uri("/api/v1/borrows/{borrowId}/return", borrowRes.bookBorrowId())
                .retrieve()
                .toBodilessEntity();
        System.out.println("반납 완료");
    }

    @Test
    void adminBorrowList() {
        // 관리자 대출 현황 조회
        BorrowPageRes pageRes = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/admin/borrows")
                        .queryParam("page", 1)
                        .queryParam("size", 10)
                        .build())
                .retrieve()
                .body(BorrowPageRes.class);
        System.out.println("pageRes = " + pageRes);
    }

    @Test
    void adminBorrowListWithStatus() {
        // 관리자 대출 현황 조회 (상태 필터)
        BorrowPageRes pageRes = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/admin/borrows")
                        .queryParam("status", "BORROWED")
                        .queryParam("page", 1)
                        .queryParam("size", 10)
                        .build())
                .retrieve()
                .body(BorrowPageRes.class);
        System.out.println("pageRes (BORROWED) = " + pageRes);
    }
}
