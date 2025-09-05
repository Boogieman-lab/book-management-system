package com.example.bookmanagementsystembo.department.presentation.controller;

import com.example.bookmanagementsystembo.department.dto.DepartmentDto;
import com.example.bookmanagementsystembo.department.presentation.dto.DepartmentCreateRequest;
import com.example.bookmanagementsystembo.department.presentation.dto.DepartmentResponse;
import com.example.bookmanagementsystembo.department.presentation.dto.DepartmentUpdateRequest;
import com.example.bookmanagementsystembo.department.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping
    public ResponseEntity<Long> createDepartment(@RequestBody DepartmentCreateRequest request) {
        return ResponseEntity.ok(departmentService.createDepartment(request.name()));
    }


    @PutMapping("/{departmentId}")
    public ResponseEntity<Void> updateDepartment(@PathVariable Long departmentId, @RequestBody DepartmentUpdateRequest request) {
        departmentService.updateDepartment(departmentId, request.name());
        return ResponseEntity.ok().build();
    }

}
