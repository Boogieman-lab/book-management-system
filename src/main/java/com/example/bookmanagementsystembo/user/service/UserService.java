package com.example.bookmanagementsystembo.user.service;

import com.example.bookmanagementsystembo.book.entity.QBook;
import com.example.bookmanagementsystembo.bookBorrow.enums.BorrowStatus;
import com.example.bookmanagementsystembo.bookBorrow.repository.BookBorrowRepository;
import com.example.bookmanagementsystembo.bookHold.entity.QBookHold;
import com.example.bookmanagementsystembo.bookRequest.entity.BookRequest;
import com.example.bookmanagementsystembo.bookRequest.repository.BookRequestRepository;
import com.example.bookmanagementsystembo.department.entity.Department;
import com.example.bookmanagementsystembo.department.repository.DepartmentRepository;
import com.example.bookmanagementsystembo.department.service.DepartmentService;
import com.example.bookmanagementsystembo.department.dto.DepartmentDto;
import com.example.bookmanagementsystembo.exception.CoreException;
import com.example.bookmanagementsystembo.exception.ErrorType;
import com.example.bookmanagementsystembo.reservation.domain.entity.QReservation;
import com.example.bookmanagementsystembo.reservation.enums.ReservationStatus;
import com.example.bookmanagementsystembo.user.dto.*;
import com.example.bookmanagementsystembo.user.entity.Users;
import com.example.bookmanagementsystembo.user.enums.Role;
import com.example.bookmanagementsystembo.user.repository.UserRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

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

    /** 사용자 ID로 기본 사용자 정보(이름, 이메일, 부서명)를 조회합니다. */
    public UserResponse read(Long userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new CoreException(ErrorType.USER_NOT_FOUND, userId));

        DepartmentDto department = departmentService.getDepartmentById(user.getDepartmentId());

        return UserResponse.of(user.getName(), user.getEmail(), department.departmentName());
    }

    /** 내 프로필 상세 정보(이름, 이메일, 부서, 프로필 이미지, 역할)를 조회합니다. */
    public UserProfileResponse getMyProfile(Long userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new CoreException(ErrorType.USER_NOT_FOUND, userId));

        String departmentName = resolveDepartmentName(user.getDepartmentId());

        return UserProfileResponse.of(user, departmentName);
    }

    /** 내 프로필(이름, 프로필 이미지)을 수정합니다. */
    @Transactional
    public UserProfileResponse updateMyProfile(Long userId, UserUpdateRequest request) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new CoreException(ErrorType.USER_NOT_FOUND, userId));

        user.updateProfile(request.name(), request.profileImage());

        String departmentName = resolveDepartmentName(user.getDepartmentId());

        return UserProfileResponse.of(user, departmentName);
    }

    /** 내 대출 이력을 페이지네이션으로 조회합니다. status가 null이면 전체 조회. */
    public UserBorrowPageResponse getMyBorrows(Long userId, int page, int size, BorrowStatus status) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<UserBorrowResponse> result = bookBorrowRepository.findByUserId(userId, status, pageable);

        return new UserBorrowPageResponse(
                result.getContent(),
                page,
                size,
                result.getTotalElements(),
                result.getTotalPages()
        );
    }

    /** 내 희망 도서 신청 이력을 페이지네이션으로 조회합니다. */
    public Page<BookRequest> getMyRequests(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        return bookRequestRepository.findAllByCondition(userId, null, pageable);
    }

    /** 내 WAITING 상태 예약 목록을 QueryDSL로 도서명 및 대기 순위와 함께 조회합니다. */
    public List<UserReservationResponse> getMyReservations(Long userId) {
        QReservation reservation = QReservation.reservation;
        QReservation reservation2 = new QReservation("reservation2");
        QBookHold bookHold = QBookHold.bookHold;
        QBook book = QBook.book;

        return queryFactory
                .select(Projections.constructor(UserReservationResponse.class,
                        reservation.reservationId,
                        book.title,
                        reservation.status,
                        reservation.reservedAt,
                        reservation.expireAt,
                        JPAExpressions
                                .select(reservation2.count().add(1))
                                .from(reservation2)
                                .where(
                                        reservation2.bookHoldId.eq(reservation.bookHoldId),
                                        reservation2.status.eq(ReservationStatus.WAITING),
                                        reservation2.reservedAt.lt(reservation.reservedAt)
                                                .or(reservation2.reservedAt.eq(reservation.reservedAt)
                                                        .and(reservation2.reservationId.lt(reservation.reservationId)))
                                )
                ))
                .from(reservation)
                .leftJoin(bookHold).on(reservation.bookHoldId.eq(bookHold.bookHoldId))
                .leftJoin(book).on(bookHold.bookId.eq(book.bookId))
                .where(
                        reservation.userId.eq(userId),
                        reservation.status.eq(ReservationStatus.WAITING)
                )
                .orderBy(reservation.reservedAt.asc())
                .fetch();
    }

    /**
     * 전체 임직원 목록을 페이지네이션으로 조회합니다 (관리자 전용).
     * 부서명은 페이지 내 고유 departmentId를 IN 절로 일괄 조회하여 N+1을 방지합니다.
     */
    public AdminUserPageResponse findAllForAdmin(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Users> result = userRepository.findAll(pageable);

        List<Long> deptIds = result.getContent().stream()
                .map(Users::getDepartmentId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        Map<Long, String> deptNameMap = departmentRepository.findAllById(deptIds).stream()
                .collect(Collectors.toMap(Department::getDepartmentId, Department::getName));

        List<AdminUserListResponse> content = result.getContent().stream()
                .map(user -> AdminUserListResponse.from(user, deptNameMap.get(user.getDepartmentId())))
                .toList();

        return new AdminUserPageResponse(content, page, size, result.getTotalElements(), result.getTotalPages());
    }

    /**
     * 임직원 권한을 변경합니다 (관리자 전용).
     */
    @Transactional
    public void updateRole(Long userId, Role role) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new CoreException(ErrorType.USER_NOT_FOUND, userId));
        user.updateRole(role);
    }

    /**
     * 잠긴 계정을 해제합니다 (관리자 전용).
     * 로그인 실패 횟수를 초기화하고 잠금을 해제합니다.
     */
    @Transactional
    public void unlockUser(Long userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new CoreException(ErrorType.USER_NOT_FOUND, userId));
        user.resetLoginFailCount();
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
