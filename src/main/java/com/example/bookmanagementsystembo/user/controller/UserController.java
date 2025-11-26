package com.example.bookmanagementsystembo.user.controller;

import com.example.bookmanagementsystembo.user.dto.UserRes;
import com.example.bookmanagementsystembo.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/users")
@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}")
    public ResponseEntity<UserRes> read(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.read(userId));
    }
}
