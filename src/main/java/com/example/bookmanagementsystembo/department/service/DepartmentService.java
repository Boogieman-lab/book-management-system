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

    public DepartmentDto getDepartmentById(Long departmentId) {
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new CoreException(ErrorType.DEPARTMENT_NOT_FOUND, departmentId));
        return DepartmentDto.from(department);
    }

    public List<DepartmentDto> getDepartments() {
        return departmentRepository.findAll().stream().map(DepartmentDto::from).toList();
    }

    @Transactional
    public Long createDepartment(String name) {
        if (departmentRepository.findByName(name).isPresent()) {
            throw new CoreException(ErrorType.DEPARTMENT_ALREADY_EXISTS, name);
        }
        Department department = Department.create(name);
        return departmentRepository.save(department).getDepartmentId();
    }

    @Transactional
    public void updateDepartment(Long departmentId, String name) {
        departmentRepository.findById(departmentId).orElseThrow(() -> new CoreException(ErrorType.DEPARTMENT_NOT_FOUND, departmentId));
        Department department = Department.update(departmentId, name);
        departmentRepository.save(department);
    }
}
