package com.example.bookmanagementsystembo.department.presentation.controller;

import com.example.bookmanagementsystembo.department.dto.DepartmentDto;
import com.example.bookmanagementsystembo.department.presentation.dto.DepartmentResponse;
import com.example.bookmanagementsystembo.department.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/departments")
public class DepartmentController {

    private final DepartmentService departmentService;

    @GetMapping("/{departmentId}")
    public ResponseEntity<DepartmentResponse> getDepartment(@PathVariable Long departmentId) {
        DepartmentDto department = departmentService.getDepartmentById(departmentId);
        return ResponseEntity.ok(DepartmentResponse.from(department));
    }

    @GetMapping
    public ResponseEntity<List<DepartmentResponse>> getDepartments() {
        List<DepartmentDto> departments = departmentService.getDepartments();
        return ResponseEntity.ok(DepartmentResponse.from(departments));
    }
}
