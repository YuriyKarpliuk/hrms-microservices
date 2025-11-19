package org.yuriy.timesheetservice.dto.request;


import org.yuriy.timesheetservice.entity.TimesheetStatus;

import java.time.LocalDate;

public record TimesheetSearchRequest(
        Long employeeId,
        TimesheetStatus status,
        LocalDate weekStartFrom,
        LocalDate weekEndTo,
        Long managerId,
        String employeeName
) {}

