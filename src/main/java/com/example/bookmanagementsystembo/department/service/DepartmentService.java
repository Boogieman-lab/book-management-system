package com.example.bookmanagementsystembo.department.service;

import com.example.bookmanagementsystembo.department.repository.DepartmentRepository;
import com.example.bookmanagementsystembo.department.dto.DepartmentInfo;
import com.example.bookmanagementsystembo.department.entity.Department;
import com.example.bookmanagementsystembo.exception.CoreException;
import com.example.bookmanagementsystembo.exception.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    public DepartmentInfo findById(Long departmentId) {
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new CoreException(ErrorType.DEPARTMENT_NOT_FOUND, departmentId));
        return DepartmentInfo.of(department.getName());
    }
}
