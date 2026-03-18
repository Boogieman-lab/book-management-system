package com.example.bookmanagementsystembo.kakaobook.service;

import com.example.bookmanagementsystembo.exception.CoreException;
import com.example.bookmanagementsystembo.exception.ErrorType;
import com.example.bookmanagementsystembo.kakaobook.dto.KakaoBookDocumentDto;
import com.example.bookmanagementsystembo.kakaobook.dto.KakaoBookMetaDto;
import com.example.bookmanagementsystembo.kakaobook.dto.KakaoBookSearchParams;
import com.example.bookmanagementsystembo.kakaobook.dto.KakaoBookSearchRes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("KakaoBookSearchService 단위 테스트")
@ExtendWith(MockitoExtension.class)
class KakaoBookSearchServiceTest {

    @Mock
    private RestClient restClient;

    @Mock
    private RestClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private RestClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    private KakaoBookSearchService service;

    @BeforeEach
    void setUp() {
        service = new KakaoBookSearchService(restClient);
        ReflectionTestUtils.setField(service, "REST_API_KEY", "KakaoAK test-key");
    }

    @Nested
    @DisplayName("search - 전체 파라미터 검색")
    class SearchTest {

        @Test
        @DisplayName("query만 있어도 정상 검색 결과를 반환한다")
        void queryOnly_returnsResult() {
            KakaoBookSearchParams params = new KakaoBookSearchParams("자바", null, null, null, null);
            KakaoBookSearchRes expected = sampleRes();

            when(restClient.get()).thenReturn(requestHeadersUriSpec);
            when(requestHeadersUriSpec.uri(any(java.net.URI.class))).thenReturn(requestHeadersSpec);
            when(requestHeadersSpec.header(any(), any())).thenReturn(requestHeadersSpec);
            when(requestHeadersSpec.accept(any())).thenReturn(requestHeadersSpec);
            when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
            when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
            when(responseSpec.body(KakaoBookSearchRes.class)).thenReturn(expected);

            KakaoBookSearchRes result = service.search(params);

            assertThat(result.documents()).hasSize(1);
            assertThat(result.documents().get(0).title()).isEqualTo("이펙티브 자바");
        }
    }

    @Nested
    @DisplayName("외부 API 오류 처리")
    class ErrorHandlingTest {

        @Test
        @DisplayName("외부 API 오류 시 BOOK_EXTERNAL_SERVICE_ERROR CoreException이 발생한다")
        void externalApiError_throwsCoreException() {
            KakaoBookSearchParams params = new KakaoBookSearchParams("자바", null, null, null, null);

            when(restClient.get()).thenReturn(requestHeadersUriSpec);
            when(requestHeadersUriSpec.uri(any(java.net.URI.class))).thenReturn(requestHeadersSpec);
            when(requestHeadersSpec.header(any(), any())).thenReturn(requestHeadersSpec);
            when(requestHeadersSpec.accept(any())).thenReturn(requestHeadersSpec);
            when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
            when(responseSpec.onStatus(any(), any())).thenAnswer(invocation -> {
                // simulate error response by throwing the exception directly
                throw new CoreException(ErrorType.BOOK_EXTERNAL_SERVICE_ERROR, params.query());
            });

            assertThatThrownBy(() -> service.search(params))
                    .isInstanceOf(CoreException.class)
                    .satisfies(ex -> assertThat(((CoreException) ex).getErrorType())
                            .isEqualTo(ErrorType.BOOK_EXTERNAL_SERVICE_ERROR));
        }
    }

    private KakaoBookSearchRes sampleRes() {
        KakaoBookDocumentDto doc = new KakaoBookDocumentDto(
                "이펙티브 자바", List.of("조슈아 블로크"), List.of(),
                "설명", null, "9788966262281", 36000, "인사이트", 32400,
                "정상판매", "http://thumb.com", "http://url.com"
        );
        KakaoBookMetaDto meta = new KakaoBookMetaDto(false, 1, 1);
        return new KakaoBookSearchRes(List.of(doc), meta);
    }
}
