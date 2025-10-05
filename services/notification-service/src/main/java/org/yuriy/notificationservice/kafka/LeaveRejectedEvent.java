package org.yuriy.notificationservice.kafka;

import java.time.LocalDate;

public record LeaveRejectedEvent(
        Long leaveId,
        String employeeEmail,
        Long employeeId,
        Long actionBy,
        LocalDate startDate,
        LocalDate endDate
) {}
