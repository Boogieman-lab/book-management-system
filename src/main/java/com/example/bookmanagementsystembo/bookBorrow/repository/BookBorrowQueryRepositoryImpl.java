package com.example.bookmanagementsystembo.bookBorrow.repository;

import com.example.bookmanagementsystembo.bookBorrow.dto.AdminBorrowRes;
import com.example.bookmanagementsystembo.bookBorrow.dto.BookBorrowDetailDto;
import com.example.bookmanagementsystembo.bookBorrow.dto.BookBorrowDto;
import com.example.bookmanagementsystembo.bookBorrow.enums.BorrowStatus;
import com.example.bookmanagementsystembo.book.entity.QBook;
import com.example.bookmanagementsystembo.bookBorrow.entity.QBookBorrow;
import com.example.bookmanagementsystembo.bookHold.entity.QBookHold;
import com.example.bookmanagementsystembo.department.entity.QDepartment;
import com.example.bookmanagementsystembo.user.entity.QUsers;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class BookBorrowQueryRepositoryImpl implements BookBorrowQueryRepository {
    private final JPAQueryFactory qf;

    private static final QBook book = QBook.book;
    private static final QBookBorrow bookBorrow = QBookBorrow.bookBorrow;
    private static final QBookHold bookHold = QBookHold.bookHold;
    private static final QUsers users = QUsers.users;
    private static final QDepartment department = QDepartment.department;

    @Override
    public List<BookBorrowDto> findBookBorrows() {
        return qf
                .select(Projections.constructor(BookBorrowDto.class,
                        bookBorrow.bookBorrowId,
                        book.title,
                        users.name,
                        bookBorrow.status,
                        bookBorrow.createdAt
                ))
                .from(bookBorrow)
                .leftJoin(bookHold).on(bookBorrow.bookHoldId.eq(bookHold.bookHoldId))
                .leftJoin(book).on(bookHold.bookId.eq(book.bookId))
                .leftJoin(users).on(bookBorrow.userId.eq(users.userId))
                .orderBy(bookBorrow.createdAt.desc())
                .fetch();
    }

    @Override
    public BookBorrowDetailDto findBookBorrow(Long bookBorrowId) {
        return qf
                .select(Projections.constructor(BookBorrowDetailDto.class,
                        bookBorrow.bookBorrowId,
                        book.title,
                        users.name,
                        bookBorrow.status,
                        bookBorrow.createdAt,
                        bookBorrow.reason,
                        department.name
                        ))
                .from(bookBorrow)
                .leftJoin(bookHold).on(bookBorrow.bookHoldId.eq(bookHold.bookHoldId))
                .leftJoin(book).on(bookHold.bookId.eq(book.bookId))
                .leftJoin(users).on(bookBorrow.userId.eq(users.userId))
                .leftJoin(department).on(users.departmentId.eq(department.departmentId))
                .where(bookBorrow.bookBorrowId.eq(bookBorrowId))
                .orderBy(bookBorrow.createdAt.desc())
                .fetchOne();
    }

    @Override
    public Page<AdminBorrowRes> findAllForAdmin(BorrowStatus status, Pageable pageable) {
        List<AdminBorrowRes> content = qf
                .select(Projections.constructor(AdminBorrowRes.class,
                        bookBorrow.bookBorrowId,
                        book.title,
                        users.name,
                        bookBorrow.status.stringValue(),
                        bookBorrow.borrowDate,
                        bookBorrow.dueDate,
                        bookBorrow.returnDate
                ))
                .from(bookBorrow)
                .leftJoin(bookHold).on(bookBorrow.bookHoldId.eq(bookHold.bookHoldId))
                .leftJoin(book).on(bookHold.bookId.eq(book.bookId))
                .leftJoin(users).on(bookBorrow.userId.eq(users.userId))
                .where(statusEq(status))
                .orderBy(bookBorrow.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = qf
                .select(bookBorrow.count())
                .from(bookBorrow)
                .where(statusEq(status));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    private BooleanExpression statusEq(BorrowStatus status) {
        return status != null ? bookBorrow.status.eq(status) : null;
    }
}
