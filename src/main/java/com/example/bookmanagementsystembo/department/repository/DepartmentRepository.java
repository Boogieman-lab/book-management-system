package com.example.bookmanagementsystembo.department.repository;

import com.example.bookmanagementsystembo.department.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
}
