package com.example.bookmanagementsystembo.user.infra;

import com.example.bookmanagementsystembo.user.domain.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
}
