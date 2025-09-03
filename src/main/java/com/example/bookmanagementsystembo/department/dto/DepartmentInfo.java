package com.example.bookmanagementsystembo.department.dto;

public record DepartmentInfo(String name) {
    public static DepartmentInfo of(String name) {
        return new DepartmentInfo(name);
    }
}
