package com.example.bookmanagementsystembo.bookRequest.repository;

import com.example.bookmanagementsystembo.bookRequest.dto.BookRequestStatus;
import com.example.bookmanagementsystembo.bookRequest.entity.BookRequest;
import com.example.bookmanagementsystembo.bookRequest.entity.QBookRequest;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class BookRequestQueryRepositoryImpl implements BookRequestQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<BookRequest> findAllByCondition(Long userId, BookRequestStatus status, Pageable pageable) {
        QBookRequest bookRequest = QBookRequest.bookRequest;

        BooleanExpression condition = null;

        if (userId != null) {
            condition = bookRequest.userId.eq(userId);
        }
        if (status != null) {
            condition = condition != null ? condition.and(bookRequest.status.eq(status)) : bookRequest.status.eq(status);
        }

        List<BookRequest> content = queryFactory
                .selectFrom(bookRequest)
                .where(condition)
                .orderBy(bookRequest.bookRequestId.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(bookRequest.count())
                .from(bookRequest)
                .where(condition)
                .fetchOne();

        return new PageImpl<>(content, pageable, total == null ? 0 : total);
    }

    @Override
    public boolean existsByIsbnAndStatus(String isbn, BookRequestStatus status) {
        QBookRequest bookRequest = QBookRequest.bookRequest;

        Integer result = queryFactory
                .selectOne()
                .from(bookRequest)
                .where(
                        bookRequest.isbn.eq(isbn),
                        bookRequest.status.eq(status)
                )
                .fetchFirst();

        return result != null;
    }
}
