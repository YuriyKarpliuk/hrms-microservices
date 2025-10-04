package org.yuriy.notificationservice.kafka;

import java.time.LocalDate;

public record TimesheetApprovedEvent(
        Long timesheetId,
        Long employeeId,
        LocalDate weekStart,
        LocalDate weekEnd,
        Double totalHours
) {
}
