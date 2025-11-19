package org.yuriy.hrms.dto.request;

import lombok.Data;

@Data
public class CreateUserNotificationRequest {
    private Long employeeId;
    private String title;
    private String message;
    private Long senderId;
    private String senderName;
}
