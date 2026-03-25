package com.example.bookmanagementsystembo.department.service;

import com.example.bookmanagementsystembo.department.dto.DepartmentDto;
import com.example.bookmanagementsystembo.department.entity.Department;
import com.example.bookmanagementsystembo.department.repository.DepartmentRepository;
import com.example.bookmanagementsystembo.exception.CoreException;
import com.example.bookmanagementsystembo.exception.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DepartmentServiceTest {

    @InjectMocks
    private DepartmentService departmentService;

    @Mock
    private DepartmentRepository departmentRepository;

    // ──────────────────────────────────────────────────────────────
    // getDepartmentById
    // ──────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("getDepartmentById")
    class GetDepartmentByIdTest {

        @Test
        @DisplayName("성공 - 존재하는 departmentId로 부서 정보를 반환한다")
        void success() {
            // Given
            Long departmentId = 1L;
            Department dept = Department.create("개발팀");
            when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(dept));

            // When
            DepartmentDto result = departmentService.getDepartmentById(departmentId);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.departmentName()).isEqualTo("개발팀");
            verify(departmentRepository).findById(departmentId);
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 departmentId면 DEPARTMENT_NOT_FOUND 예외 발생")
        void fail_notFound() {
            // Given
            Long departmentId = 999L;
            when(departmentRepository.findById(departmentId)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> departmentService.getDepartmentById(departmentId))
                    .isInstanceOf(CoreException.class)
                    .satisfies(ex -> assertThat(((CoreException) ex).getErrorType())
                            .isEqualTo(ErrorType.DEPARTMENT_NOT_FOUND));
        }
    }

    // ──────────────────────────────────────────────────────────────
    // getDepartments
    // ──────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("getDepartments")
    class GetDepartmentsTest {

        @Test
        @DisplayName("성공 - 전체 부서 목록을 반환한다")
        void success() {
            // Given
            Department dept1 = Department.create("개발팀");
            Department dept2 = Department.create("기획팀");
            when(departmentRepository.findAll()).thenReturn(List.of(dept1, dept2));

            // When
            List<DepartmentDto> result = departmentService.getDepartments();

            // Then
            assertThat(result).hasSize(2);
            assertThat(result).extracting(DepartmentDto::departmentName)
                    .containsExactlyInAnyOrder("개발팀", "기획팀");
            verify(departmentRepository).findAll();
        }

        @Test
        @DisplayName("성공 - 부서가 없으면 빈 리스트를 반환한다")
        void success_empty() {
            // Given
            when(departmentRepository.findAll()).thenReturn(List.of());

            // When
            List<DepartmentDto> result = departmentService.getDepartments();

            // Then
            assertThat(result).isEmpty();
        }
    }

    // ──────────────────────────────────────────────────────────────
    // createDepartment
    // ──────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("createDepartment")
    class CreateDepartmentTest {

        @Test
        @DisplayName("성공 - 중복 없는 이름으로 부서를 생성하고 ID를 반환한다")
        void success() {
            // Given
            String name = "QA팀";
            Department saved = Department.create(name);
            when(departmentRepository.findByName(name)).thenReturn(Optional.empty());
            when(departmentRepository.save(any(Department.class))).thenReturn(saved);

            // When
            departmentService.createDepartment(name);

            // Then
            verify(departmentRepository).findByName(name);
            verify(departmentRepository).save(any(Department.class));
        }

        @Test
        @DisplayName("실패 - 이미 존재하는 이름이면 DEPARTMENT_ALREADY_EXISTS 예외 발생")
        void fail_duplicate() {
            // Given
            String name = "개발팀";
            when(departmentRepository.findByName(name)).thenReturn(Optional.of(Department.create(name)));

            // When & Then
            assertThatThrownBy(() -> departmentService.createDepartment(name))
                    .isInstanceOf(CoreException.class)
                    .satisfies(ex -> assertThat(((CoreException) ex).getErrorType())
                            .isEqualTo(ErrorType.DEPARTMENT_ALREADY_EXISTS));
            verify(departmentRepository, never()).save(any());
        }
    }

    // ──────────────────────────────────────────────────────────────
    // updateDepartment
    // ──────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("updateDepartment")
    class UpdateDepartmentTest {

        @Test
        @DisplayName("성공 - 중복 없는 이름으로 부서명을 수정한다")
        void success() {
            // Given
            Long departmentId = 1L;
            String newName = "플랫폼팀";
            Department dept = Department.create("개발팀");
            when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(dept));
            when(departmentRepository.findByName(newName)).thenReturn(Optional.empty());

            // When
            departmentService.updateDepartment(departmentId, newName);

            // Then
            assertThat(dept.getName()).isEqualTo(newName);
            verify(departmentRepository).findById(departmentId);
            verify(departmentRepository).findByName(newName);
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 departmentId면 DEPARTMENT_NOT_FOUND 예외 발생")
        void fail_notFound() {
            // Given
            Long departmentId = 999L;
            when(departmentRepository.findById(departmentId)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> departmentService.updateDepartment(departmentId, "새이름"))
                    .isInstanceOf(CoreException.class)
                    .satisfies(ex -> assertThat(((CoreException) ex).getErrorType())
                            .isEqualTo(ErrorType.DEPARTMENT_NOT_FOUND));
        }

        @Test
        @DisplayName("실패 - 변경할 이름이 이미 존재하면 DEPARTMENT_ALREADY_EXISTS 예외 발생")
        void fail_duplicateName() {
            // Given
            Long departmentId = 1L;
            String newName = "기획팀";
            when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(Department.create("개발팀")));
            when(departmentRepository.findByName(newName)).thenReturn(Optional.of(Department.create(newName)));

            // When & Then
            assertThatThrownBy(() -> departmentService.updateDepartment(departmentId, newName))
                    .isInstanceOf(CoreException.class)
                    .satisfies(ex -> assertThat(((CoreException) ex).getErrorType())
                            .isEqualTo(ErrorType.DEPARTMENT_ALREADY_EXISTS));
        }
    }
}
