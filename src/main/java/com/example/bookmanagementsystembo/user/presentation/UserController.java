package com.example.bookmanagementsystembo.user.presentation;

import com.example.bookmanagementsystembo.user.domain.dto.UserInfo;
import com.example.bookmanagementsystembo.user.domain.service.UserService;
import com.example.bookmanagementsystembo.user.presentation.dto.UserInfoResponse;
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
    public ResponseEntity<UserInfoResponse> getUserInfo(@PathVariable Long userId) {
        UserInfo userInfo = userService.findByUser(userId);
        return ResponseEntity.ok(UserInfoResponse.from(userInfo));
    }
}
