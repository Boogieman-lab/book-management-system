package com.example.bookmanagementsystembo.department.presentation.dto;

import com.example.bookmanagementsystembo.department.dto.DepartmentDto;

import java.util.List;

public record DepartmentResponse(Long id, String name) {
    public static DepartmentResponse from(DepartmentDto dto) {
        return new DepartmentResponse(dto.departmentId(), dto.departmentName());
    }
    public static List<DepartmentResponse> from(List<DepartmentDto> dtos) {
        return dtos.stream()
                .map(DepartmentResponse::from)
                .toList();
    }
}
