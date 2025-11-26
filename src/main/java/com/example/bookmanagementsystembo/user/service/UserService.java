package com.example.bookmanagementsystembo.user.service;

import com.example.bookmanagementsystembo.department.dto.DepartmentDto;
import com.example.bookmanagementsystembo.department.service.DepartmentService;
import com.example.bookmanagementsystembo.exception.CoreException;
import com.example.bookmanagementsystembo.exception.ErrorType;
import com.example.bookmanagementsystembo.user.dto.UserRes;
import com.example.bookmanagementsystembo.user.entity.Users;
import com.example.bookmanagementsystembo.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    private final DepartmentService departmentService;

    public UserRes read(Long userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new CoreException(ErrorType.USER_NOT_FOUND, userId));

        DepartmentDto department = departmentService.getDepartmentById(user.getDepartmentId());

        return UserRes.of(user.getName(), user.getEmail(), department.departmentName());
    }
}
