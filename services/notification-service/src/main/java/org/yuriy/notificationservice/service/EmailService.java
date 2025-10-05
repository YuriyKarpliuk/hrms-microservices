package org.yuriy.notificationservice.service;

public interface EmailService {
    void sendEmail(String to, String subject, String text);
}
