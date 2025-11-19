package org.yuriy.notificationservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class BirthdayScheduler {

    private final BirthdayNotificationService birthdayService;

    @Scheduled(cron = "0 0 10 * * *")
    public void sendBirthdayNotifs() {
        birthdayService.sendBirthdayNotifications();
    }
}
