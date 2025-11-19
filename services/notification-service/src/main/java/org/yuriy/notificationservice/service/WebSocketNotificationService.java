package org.yuriy.notificationservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.yuriy.notificationservice.entity.UserNotification;

@Service
@RequiredArgsConstructor
public class WebSocketNotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public void sendToUser(Long employeeId, UserNotification notification) {
        String destination = "/topic/notifications/" + employeeId;
        messagingTemplate.convertAndSend(destination, notification);
    }
}
