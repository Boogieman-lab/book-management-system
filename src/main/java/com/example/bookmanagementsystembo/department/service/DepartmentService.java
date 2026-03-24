package com.example.bookmanagementsystembo.department.service;

import com.example.bookmanagementsystembo.department.repository.DepartmentRepository;
import com.example.bookmanagementsystembo.department.dto.DepartmentDto;
import com.example.bookmanagementsystembo.department.entity.Department;
import com.example.bookmanagementsystembo.exception.CoreException;
import com.example.bookmanagementsystembo.exception.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    /** 부서 ID로 단건 부서 정보를 조회합니다. 존재하지 않으면 예외를 발생시킵니다. */
    public DepartmentDto getDepartmentById(Long departmentId) {
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new CoreException(ErrorType.DEPARTMENT_NOT_FOUND, departmentId));
        return DepartmentDto.from(department);
    }

    /** 전체 부서 목록을 반환합니다. */
    public List<DepartmentDto> getDepartments() {
        return departmentRepository.findAll().stream().map(DepartmentDto::from).toList();
    }

    /**
     * 부서를 생성합니다.
     * 이미 동일한 이름의 부서가 존재하면 예외를 발생시킵니다.
     * @return 생성된 departmentId
     */
    @Transactional
    public Long createDepartment(String name) {
        if (departmentRepository.findByName(name).isPresent()) {
            throw new CoreException(ErrorType.DEPARTMENT_ALREADY_EXISTS, name);
        }
        Department department = Department.create(name);
        return departmentRepository.save(department).getDepartmentId();
    }

    /**
     * 부서명을 수정합니다.
     * 대상 부서가 없거나 변경할 이름이 이미 존재하면 예외를 발생시킵니다.
     */
    @Transactional
    public void updateDepartment(Long departmentId, String name) {
        Department department = departmentRepository.findById(departmentId).orElseThrow(() -> new CoreException(ErrorType.DEPARTMENT_NOT_FOUND, departmentId));

        if (departmentRepository.findByName(name).isPresent()) {
            throw new CoreException(ErrorType.DEPARTMENT_ALREADY_EXISTS, name);
        }
        department.update(name);
    }
}
