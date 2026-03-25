package com.example.bookmanagementsystembo.book.repository;

import com.example.bookmanagementsystembo.book.dto.BookHoldCountDto;
import com.example.bookmanagementsystembo.book.dto.BookSearchCond;
import com.example.bookmanagementsystembo.book.entity.Book;
import com.example.bookmanagementsystembo.book.entity.QBook;
import com.example.bookmanagementsystembo.book.enums.BookSearchField;
import com.example.bookmanagementsystembo.bookHold.entity.QBookHold;
import com.example.bookmanagementsystembo.bookHold.enums.BookHoldStatus;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * QueryDSL 기반 도서 동적 검색 구현체.
 *
 * <p>검색 필드(field)에 따른 동적 WHERE 조건:
 * <ul>
 *   <li>TITLE     : title LIKE '%keyword%' (대소문자 무시)</li>
 *   <li>AUTHOR    : authors JSON 컬럼 LIKE '%keyword%'</li>
 *   <li>ISBN      : isbn = 'keyword' (정확히 일치)</li>
 *   <li>PUBLISHER : publisher LIKE '%keyword%'</li>
 *   <li>null(미지정): 제목 + 저자 + ISBN OR 통합 검색</li>
 * </ul>
 * keyword가 비어있으면 WHERE 조건 없이 전체 조회합니다.
 * 정렬: title 가나다(ASC) 고정.
 */
@Repository
@RequiredArgsConstructor
public class BookQueryRepositoryImpl implements BookQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Book> searchBooks(BookSearchCond cond, Pageable pageable) {
        QBook book = QBook.book;
        BooleanExpression condition = buildCondition(book, cond.keyword(), cond.field());

        List<Book> content = queryFactory
                .selectFrom(book)
                .where(condition)
                .orderBy(book.title.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(book.count())
                .from(book)
                .where(condition)
                .fetchOne();

        return new PageImpl<>(content, pageable, total == null ? 0 : total);
    }

    @Override
    public Map<Long, BookHoldCountDto> countHoldsByBookIds(List<Long> bookIds) {
        if (bookIds.isEmpty()) return Map.of();

        QBookHold bh = QBookHold.bookHold;
        NumberExpression<Long> availableExpr = Expressions.cases()
                .when(bh.status.eq(BookHoldStatus.AVAILABLE)).then(1L)
                .otherwise(0L).sum();

        return queryFactory
                .select(bh.bookId, bh.bookHoldId.count(), availableExpr)
                .from(bh)
                .where(bh.bookId.in(bookIds))
                .groupBy(bh.bookId)
                .fetch()
                .stream()
                .collect(Collectors.toMap(
                        t -> t.get(bh.bookId),
                        t -> new BookHoldCountDto(
                                Objects.requireNonNullElse(t.get(bh.bookHoldId.count()), 0L),
                                Objects.requireNonNullElse(t.get(availableExpr), 0L)
                        )
                ));
    }

    /**
     * 검색 필드와 키워드로 동적 BooleanExpression을 생성합니다.
     * keyword가 null/blank이면 null을 반환해 WHERE 조건을 생략합니다.
     */
    private BooleanExpression buildCondition(QBook book, String keyword, BookSearchField field) {
        if (!StringUtils.hasText(keyword)) {
            return null;
        }

        if (field == null) {
            // 검색 필드 미지정: 제목 + 저자 + ISBN 통합 검색 (OR)
            return book.title.containsIgnoreCase(keyword)
                    .or(book.author.containsIgnoreCase(keyword))
                    .or(book.isbn13.eq(keyword));
        }

        return switch (field) {
            case TITLE     -> book.title.containsIgnoreCase(keyword);
            case AUTHOR    -> book.author.containsIgnoreCase(keyword);
            case ISBN      -> book.isbn13.eq(keyword);
            case PUBLISHER -> book.publisher.containsIgnoreCase(keyword);
        };
    }
}
