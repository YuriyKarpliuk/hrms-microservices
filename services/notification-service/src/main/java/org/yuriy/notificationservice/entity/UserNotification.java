package org.yuriy.notificationservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long employeeId;
    private String title;
    private String message;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    @Column
    private Long senderId;

    @Column
    private String senderName;

    private boolean read = false;
    private LocalDateTime createdAt = LocalDateTime.now();
}
