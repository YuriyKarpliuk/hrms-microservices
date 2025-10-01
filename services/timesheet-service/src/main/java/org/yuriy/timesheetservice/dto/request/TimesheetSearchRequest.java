package org.yuriy.timesheetservice.dto.request;


import org.yuriy.timesheetservice.entity.TimesheetStatus;

import java.time.LocalDate;

public record TimesheetSearchRequest(Long employeeId,
        LocalDate startDateFrom,
        LocalDate startDateTo,
        LocalDate endDateFrom,
        LocalDate endDateTo,
        TimesheetStatus status) {
}
