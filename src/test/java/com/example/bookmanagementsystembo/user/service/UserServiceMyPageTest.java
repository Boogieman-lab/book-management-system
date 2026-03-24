package com.example.bookmanagementsystembo.user.service;

import com.example.bookmanagementsystembo.bookBorrow.enums.BorrowStatus;
import com.example.bookmanagementsystembo.bookBorrow.repository.BookBorrowRepository;
import com.example.bookmanagementsystembo.bookRequest.dto.BookRequestStatus;
import com.example.bookmanagementsystembo.bookRequest.entity.BookRequest;
import com.example.bookmanagementsystembo.bookRequest.repository.BookRequestRepository;
import com.example.bookmanagementsystembo.department.entity.Department;
import com.example.bookmanagementsystembo.department.repository.DepartmentRepository;
import com.example.bookmanagementsystembo.department.service.DepartmentService;
import com.example.bookmanagementsystembo.exception.CoreException;
import com.example.bookmanagementsystembo.reservation.enums.ReservationStatus;
import com.example.bookmanagementsystembo.user.dto.*;
import com.example.bookmanagementsystembo.user.entity.Users;
import com.example.bookmanagementsystembo.user.enums.Role;
import com.example.bookmanagementsystembo.user.repository.UserRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceMyPageTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private DepartmentService departmentService;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private BookBorrowRepository bookBorrowRepository;

    @Mock
    private BookRequestRepository bookRequestRepository;

    @Mock
    private JPAQueryFactory queryFactory;

    private Users testUser;
    private Department testDepartment;

    @BeforeEach
    void setUp() {
        testUser = Users.create("test@example.com", "password", "홍길동", 1L, "profile.jpg", Role.ROLE_USER);
        testDepartment = Department.create("개발팀");
    }

    @Test
    @DisplayName("프로필 조회 성공")
    void getMyProfile_success() {
        // Given
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(testDepartment));

        // When
        UserProfileResponse result = userService.getMyProfile(userId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.email()).isEqualTo("test@example.com");
        assertThat(result.name()).isEqualTo("홍길동");
        assertThat(result.departmentName()).isEqualTo("개발팀");
        assertThat(result.profileImage()).isEqualTo("profile.jpg");
        assertThat(result.role()).isEqualTo(Role.ROLE_USER);
        verify(userRepository).findById(userId);
    }

    @Test
    @DisplayName("프로필 조회 실패 - 사용자 없음")
    void getMyProfile_fail_userNotFound() {
        // Given
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(CoreException.class, () -> userService.getMyProfile(userId));
    }

    @Test
    @DisplayName("프로필 수정 성공")
    void updateMyProfile_success() {
        // Given
        Long userId = 1L;
        UserUpdateRequest request = new UserUpdateRequest("김철수", "new-profile.jpg");
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(testDepartment));

        // When
        UserProfileResponse result = userService.updateMyProfile(userId, request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("김철수");
        assertThat(result.profileImage()).isEqualTo("new-profile.jpg");
        assertThat(result.departmentName()).isEqualTo("개발팀");
        verify(userRepository).findById(userId);
    }

    @Test
    @DisplayName("내 대출 이력 조회")
    void getMyBorrows_success() {
        // Given
        Long userId = 1L;
        LocalDateTime now = LocalDateTime.now();
        UserBorrowResponse borrowRes = new UserBorrowResponse("자바의 정석", 10L, now, now.plusDays(14), null, "BORROWED");
        Page<UserBorrowResponse> page = new PageImpl<>(List.of(borrowRes), PageRequest.of(0, 10), 1);
        when(bookBorrowRepository.findByUserId(eq(userId), any(), any())).thenReturn(page);

        // When
        UserBorrowPageResponse result = userService.getMyBorrows(userId, 1, 10, null);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.content()).hasSize(1);
        assertThat(result.content().get(0).bookTitle()).isEqualTo("자바의 정석");
        assertThat(result.totalElements()).isEqualTo(1);
        verify(bookBorrowRepository).findByUserId(eq(userId), any(), any());
    }

    @Test
    @DisplayName("내 신청 내역 조회")
    void getMyRequests_success() {
        // Given
        Long userId = 1L;
        BookRequest bookRequest = BookRequest.create(userId, "클린 코드", "로버트 마틴", "인사이트", "9788966260959", "학습용");
        Page<BookRequest> page = new PageImpl<>(List.of(bookRequest), PageRequest.of(0, 10), 1);
        when(bookRequestRepository.findAllByCondition(eq(userId), any(), any())).thenReturn(page);

        // When
        Page<BookRequest> result = userService.getMyRequests(userId, 1, 10);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("클린 코드");
        verify(bookRequestRepository).findAllByCondition(eq(userId), any(), any());
    }

    // getMyReservations는 JPAQueryFactory 체이닝 구조를 직접 사용하므로
    // 단위 테스트에서 의미 있는 검증이 어렵습니다. 통합 테스트에서 검증합니다.

    @Test
    @DisplayName("프로필 조회 - departmentId가 null인 경우")
    void getMyProfile_nullDepartment() {
        // Given
        Long userId = 1L;
        Users userNoDept = Users.create("test@example.com", "password", "홍길동", null, "profile.jpg", Role.ROLE_USER);
        when(userRepository.findById(userId)).thenReturn(Optional.of(userNoDept));

        // When
        UserProfileResponse result = userService.getMyProfile(userId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.departmentName()).isNull();
        verify(departmentRepository, never()).findById(any());
    }
}
