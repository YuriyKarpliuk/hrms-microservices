package org.yuriy.timesheetservice.dto.response;


import org.yuriy.timesheetservice.entity.TimesheetStatus;

import java.time.LocalDate;
import java.util.List;

public record TimesheetResponse(
        Long id,
        Long employeeId,
        String employeeFirstName,
        String employeeLastName,
        LocalDate weekStart,
        LocalDate weekEnd,
        Double totalHours,
        TimesheetStatus status,
        List<TimesheetEntryResponse> entries
) {}
