package org.yuriy.timesheetservice.kafka;

import java.time.LocalDate;

public record TimesheetApprovedEvent(
        Long timesheetId,
        Long employeeId,
        LocalDate weekStart,
        LocalDate weekEnd,
        Double totalHours
) {
}
