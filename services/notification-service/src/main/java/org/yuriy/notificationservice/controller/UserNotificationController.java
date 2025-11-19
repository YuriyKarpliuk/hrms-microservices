package org.yuriy.notificationservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.yuriy.notificationservice.dto.request.CreateUserNotificationRequest;
import org.yuriy.notificationservice.entity.UserNotification;
import org.yuriy.notificationservice.repository.UserNotificationRepository;
import org.yuriy.notificationservice.service.WebSocketNotificationService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/user-notifications")
@RequiredArgsConstructor
public class UserNotificationController {

    private final UserNotificationRepository userNotificationRepository;

    private final WebSocketNotificationService webSocketNotificationService;

    @GetMapping("/{employeeId}")
    public ResponseEntity<List<UserNotification>> getNotifications(@PathVariable Long employeeId) {
        return ResponseEntity.ok(
                userNotificationRepository.findByEmployeeIdOrderByCreatedAtDesc(employeeId)
        );
    }

    @PostMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        userNotificationRepository.findById(id).ifPresent(n -> {
            n.setRead(true);
            userNotificationRepository.save(n);
        });
        return ResponseEntity.ok().build();
    }

    @PostMapping("/read-all/{employeeId}")
    public ResponseEntity<Void> markAllAsRead(@PathVariable Long employeeId) {
        userNotificationRepository.findByEmployeeIdOrderByCreatedAtDesc(employeeId)
                .forEach(n -> {
                    if (!n.isRead()) {
                        n.setRead(true);
                    }
                });
        userNotificationRepository.flush();
        return ResponseEntity.ok().build();
    }
    @GetMapping("/{employeeId}/unread")
    public ResponseEntity<List<UserNotification>> getUnread(@PathVariable Long employeeId) {
        return ResponseEntity.ok(
                userNotificationRepository.findByEmployeeIdAndReadFalseOrderByCreatedAtDesc(employeeId)
        );
    }

    @PostMapping
    public ResponseEntity<UserNotification> createNotification(
            @RequestBody CreateUserNotificationRequest request
    ) {

        UserNotification notif = UserNotification.builder()
                .employeeId(request.getEmployeeId())
                .title(request.getTitle())
                .message(request.getMessage())
                .senderId(request.getSenderId())
                .senderName(request.getSenderName())
                .read(false)
                .createdAt(LocalDateTime.now())
                .build();

        notif = userNotificationRepository.save(notif);

        webSocketNotificationService.sendToUser(request.getEmployeeId(), notif);

        return ResponseEntity.ok(notif);
    }


}
