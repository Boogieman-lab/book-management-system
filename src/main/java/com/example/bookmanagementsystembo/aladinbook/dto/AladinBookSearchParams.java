package com.example.bookmanagementsystembo.aladinbook.dto;

/**
 * 알라딘 도서 검색 요청 파라미터.
 *
 * @param query      검색어 (필수)
 * @param queryType  검색 필드: title | author | publisher | keyword (기본값)
 * @param start      페이지 번호 1-based (기본값 1, 알라딘 총 200개 제한)
 * @param maxResults 페이지당 결과 수 1~50 (기본값 10)
 */
public record AladinBookSearchParams(
        String query,
        String queryType,
        Integer start,
        Integer maxResults
) {
}
