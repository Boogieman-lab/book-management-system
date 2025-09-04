package com.example.bookmanagementsystembo.department.dto;

import com.example.bookmanagementsystembo.department.entity.Department;

public record DepartmentDto(Long departmentId, String departmentName) {
    public static DepartmentDto from(Department department) {
        return new DepartmentDto(department.getDepartmentId(), department.getName());
    }
}
