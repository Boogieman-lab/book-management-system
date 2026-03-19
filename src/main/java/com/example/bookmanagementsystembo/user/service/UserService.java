package com.example.bookmanagementsystembo.user.service;

import com.example.bookmanagementsystembo.book.entity.QBook;
import com.example.bookmanagementsystembo.bookBorrow.enums.BorrowStatus;
import com.example.bookmanagementsystembo.bookBorrow.repository.BookBorrowRepository;
import com.example.bookmanagementsystembo.bookHold.entity.QBookHold;
import com.example.bookmanagementsystembo.bookrequest.entity.BookRequest;
import com.example.bookmanagementsystembo.bookrequest.repository.BookRequestRepository;
import com.example.bookmanagementsystembo.department.repository.DepartmentRepository;
import com.example.bookmanagementsystembo.department.service.DepartmentService;
import com.example.bookmanagementsystembo.department.dto.DepartmentDto;
import com.example.bookmanagementsystembo.exception.CoreException;
import com.example.bookmanagementsystembo.exception.ErrorType;
import com.example.bookmanagementsystembo.reservation.domain.entity.QReservation;
import com.example.bookmanagementsystembo.reservation.enums.ReservationStatus;
import com.example.bookmanagementsystembo.user.dto.*;
import com.example.bookmanagementsystembo.user.entity.Users;
import com.example.bookmanagementsystembo.user.repository.UserRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final DepartmentService departmentService;
    private final DepartmentRepository departmentRepository;
    private final BookBorrowRepository bookBorrowRepository;
    private final BookRequestRepository bookRequestRepository;
    private final JPAQueryFactory queryFactory;

    public UserRes read(Long userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new CoreException(ErrorType.USER_NOT_FOUND, userId));

        DepartmentDto department = departmentService.getDepartmentById(user.getDepartmentId());

        return UserRes.of(user.getName(), user.getEmail(), department.departmentName());
    }

    public UserProfileRes getMyProfile(Long userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new CoreException(ErrorType.USER_NOT_FOUND, userId));

        String departmentName = resolveDepartmentName(user.getDepartmentId());

        return UserProfileRes.of(user, departmentName);
    }

    @Transactional
    public UserProfileRes updateMyProfile(Long userId, UserUpdateReq request) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new CoreException(ErrorType.USER_NOT_FOUND, userId));

        user.updateProfile(request.name(), request.profileImage());

        String departmentName = resolveDepartmentName(user.getDepartmentId());

        return UserProfileRes.of(user, departmentName);
    }

    public UserBorrowPageRes getMyBorrows(Long userId, int page, int size, BorrowStatus status) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<UserBorrowRes> result = bookBorrowRepository.findByUserId(userId, status, pageable);

        return new UserBorrowPageRes(
                result.getContent(),
                page,
                size,
                result.getTotalElements(),
                result.getTotalPages()
        );
    }

    public Page<BookRequest> getMyRequests(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        return bookRequestRepository.findAllByCondition(userId, null, pageable);
    }

    public List<UserReservationRes> getMyReservations(Long userId) {
        QReservation reservation = QReservation.reservation;
        QBookHold bookHold = QBookHold.bookHold;
        QBook book = QBook.book;

        return queryFactory
                .select(Projections.constructor(UserReservationRes.class,
                        reservation.reservationId,
                        book.title,
                        reservation.status,
                        reservation.reservedAt,
                        reservation.expireAt
                ))
                .from(reservation)
                .leftJoin(bookHold).on(reservation.bookHold.bookHoldId.eq(bookHold.bookHoldId))
                .leftJoin(book).on(bookHold.bookId.eq(book.bookId))
                .where(
                        reservation.userId.eq(userId),
                        reservation.status.eq(ReservationStatus.WAITING)
                )
                .orderBy(reservation.reservedAt.desc())
                .fetch();
    }

    private String resolveDepartmentName(Long departmentId) {
        if (departmentId == null) {
            return null;
        }
        return departmentRepository.findById(departmentId)
                .map(dept -> dept.getName())
                .orElse(null);
    }
}
