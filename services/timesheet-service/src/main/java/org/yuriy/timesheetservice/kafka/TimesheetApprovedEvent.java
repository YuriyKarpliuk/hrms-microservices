package org.yuriy.timesheetservice.kafka;

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
