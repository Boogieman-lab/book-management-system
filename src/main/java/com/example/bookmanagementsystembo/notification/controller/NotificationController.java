package com.example.bookmanagementsystembo.notification.controller;

import com.example.bookmanagementsystembo.common.SecurityUtils;
import com.example.bookmanagementsystembo.notification.dto.NotificationPageRes;
import com.example.bookmanagementsystembo.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<NotificationPageRes> getMyNotifications(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "unreadOnly", defaultValue = "false") boolean unreadOnly
    ) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(notificationService.getMyNotifications(userId, page, size, unreadOnly));
    }

    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long notificationId) {
        Long userId = SecurityUtils.getCurrentUserId();
        notificationService.markAsRead(notificationId, userId);
        return ResponseEntity.ok().build();
    }
}
