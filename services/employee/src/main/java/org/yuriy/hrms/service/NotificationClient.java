package org.yuriy.hrms.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.yuriy.hrms.dto.request.CreateUserNotificationRequest;

@FeignClient(name = "notification-service")
public interface NotificationClient {

    @PostMapping("/api/v1/user-notifications")
    void sendNotification(@RequestBody CreateUserNotificationRequest req);
}
