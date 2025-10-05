package org.yuriy.leaveservice.kafka;

import java.time.LocalDate;

public record LeaveApprovedEvent(
        Long leaveId,
        String employeeEmail,
        Long employeeId,
        LocalDate startDate,
        LocalDate endDate,
        String type,
        Long actionBy
) {
}

