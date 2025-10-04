package org.yuriy.notificationservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.yuriy.notificationservice.entity.Notification;
import org.yuriy.notificationservice.entity.NotificationStatus;
import org.yuriy.notificationservice.repository.NotificationRepository;
import org.yuriy.notificationservice.service.EmailService;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;
    private final NotificationRepository notificationRepository;

    @Override
    public void sendEmail(String to, String subject, String text) {
        Notification notification = Notification.builder()
                .recipient(to)
                .subject(subject)
                .message(text)
                .status(NotificationStatus.PENDING)
                .build();

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);

            notification.setStatus(NotificationStatus.SENT);
            log.info("Email sent to {}", to);
        } catch (Exception e) {
            notification.setStatus(NotificationStatus.FAILED);
            log.error("Failed to send email to {}: {}", to, e.getMessage());
        }

        notificationRepository.save(notification);
    }
}
