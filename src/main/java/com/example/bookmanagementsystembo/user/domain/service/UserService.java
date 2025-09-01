package com.example.bookmanagementsystembo.user.domain.service;

import com.example.bookmanagementsystembo.exception.CoreException;
import com.example.bookmanagementsystembo.exception.ErrorType;
import com.example.bookmanagementsystembo.user.domain.dto.UserInfo;
import com.example.bookmanagementsystembo.user.domain.entity.Department;
import com.example.bookmanagementsystembo.user.domain.entity.Users;
import com.example.bookmanagementsystembo.user.infra.DepartmentRepository;
import com.example.bookmanagementsystembo.user.infra.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    private final DepartmentRepository departmentRepository;

    public UserInfo findByUser(Long userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new CoreException(ErrorType.USER_NOT_FOUND, userId));

        Department department = departmentRepository.findById(user.getDepartmentId())
                .orElseThrow(() -> new CoreException(ErrorType.DEPARTMENT_NOT_FOUND, user.getDepartmentId()));

        return UserInfo.of(user.getName(), user.getEmail(), department.getName());
    }
}
