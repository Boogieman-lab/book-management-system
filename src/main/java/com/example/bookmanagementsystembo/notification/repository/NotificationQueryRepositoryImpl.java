package com.example.bookmanagementsystembo.notification.repository;

import com.example.bookmanagementsystembo.notification.entity.Notification;
import com.example.bookmanagementsystembo.notification.entity.QNotification;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class NotificationQueryRepositoryImpl implements NotificationQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Notification> findByUserId(Long userId, boolean unreadOnly, long offset, int size) {
        QNotification notification = QNotification.notification;

        BooleanExpression condition = notification.userId.eq(userId);
        if (unreadOnly) {
            condition = condition.and(notification.isRead.eq(false));
        }

        return queryFactory
                .selectFrom(notification)
                .where(condition)
                .orderBy(notification.isRead.asc(), notification.createdAt.desc())
                .offset(offset)
                .limit(size)
                .fetch();
    }

    @Override
    public long countByUserId(Long userId, boolean unreadOnly) {
        QNotification notification = QNotification.notification;

        BooleanExpression condition = notification.userId.eq(userId);
        if (unreadOnly) {
            condition = condition.and(notification.isRead.eq(false));
        }

        Long count = queryFactory
                .select(notification.count())
                .from(notification)
                .where(condition)
                .fetchOne();

        return count == null ? 0 : count;
    }
}
