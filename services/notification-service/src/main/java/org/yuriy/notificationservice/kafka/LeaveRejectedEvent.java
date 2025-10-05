package org.yuriy.notificationservice.kafka;

import java.time.LocalDate;

public record LeaveRejectedEvent(
        Long leaveId,
        Long employeeId,
        Long actionBy,
        LocalDate startDate,
        LocalDate endDate
) {}
