package org.yuriy.timesheetservice.kafka;

import java.time.LocalDate;

public record LeaveRequestedEvent(Long leaveId, String employeeEmail,
        Long employeeId,
        LocalDate from, LocalDate to, String type,
        String reason) {
}
