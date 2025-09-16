package com.example.bookmanagementsystembo.book.infra;

import com.example.bookmanagementsystembo.book.dto.BookBorrowDetailDto;
import com.example.bookmanagementsystembo.book.dto.BookBorrowDto;
import com.example.bookmanagementsystembo.book.entity.QBookBorrow;
import com.example.bookmanagementsystembo.book.entity.QBookHold;
import com.example.bookmanagementsystembo.department.entity.QDepartment;
import com.example.bookmanagementsystembo.user.domain.entity.QUsers;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class BookBorrowQueryRepositoryImpl implements BookBorrowQueryRepository {
    private final JPAQueryFactory qf;

    private static final QBookBorrow bookBorrow = QBookBorrow.bookBorrow;
    private static final QBookHold bookHold = QBookHold.bookHold;
    private static final QUsers users = QUsers.users;
    private static final QDepartment department = QDepartment.department;

    @Override
    public List<BookBorrowDto> findBookBorrows() {
        return qf
                .select(Projections.constructor(BookBorrowDto.class,
                        bookBorrow.bookBorrowId,
                        bookHold.title,
                        users.name,
                        bookBorrow.status,
                        bookBorrow.createdAt
                ))
                .from(bookBorrow)
                .leftJoin(bookHold).on(bookBorrow.bookHoldId.eq(bookHold.bookHoldId))
                .leftJoin(users).on(bookBorrow.userId.eq(users.userId))
                .orderBy(bookBorrow.createdAt.desc())
                .fetch();
    }

    @Override
    public BookBorrowDetailDto findBookBorrow(Long bookBorrowId) {
        return qf
                .select(Projections.constructor(BookBorrowDetailDto.class,
                        bookBorrow.bookBorrowId,
                        bookHold.title,
                        users.name,
                        bookBorrow.status,
                        bookBorrow.createdAt,
                        bookBorrow.reason,
                        department.name
                        ))
                .from(bookBorrow)
                .leftJoin(bookHold).on(bookBorrow.bookHoldId.eq(bookHold.bookHoldId))
                .leftJoin(users).on(bookBorrow.userId.eq(users.userId))
                .leftJoin(department).on(users.departmentId.eq(department.departmentId))
                .where(bookBorrow.bookBorrowId.eq(bookBorrowId))
                .orderBy(bookBorrow.createdAt.desc())
                .fetchOne();
    }
}
