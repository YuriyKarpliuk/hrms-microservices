package org.yuriy.notificationservice.kafka;

import java.time.LocalDate;

public record TimesheetApprovedEvent(
        Long timesheetId,
        String employeeEmail,
        Long employeeId,
        LocalDate weekStart,
        LocalDate weekEnd,
        Double totalHours
) {
}
