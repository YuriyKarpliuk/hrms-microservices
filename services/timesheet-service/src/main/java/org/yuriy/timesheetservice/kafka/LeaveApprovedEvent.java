package org.yuriy.timesheetservice.kafka;

import java.time.LocalDate;

public record LeaveApprovedEvent(
        Long leaveId,
        Long employeeId,
        LocalDate startDate,
        LocalDate endDate,
        String type,
        Long actionBy
) {
}
