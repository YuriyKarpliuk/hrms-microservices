package org.yuriy.timesheetservice.dto.response;


import java.time.LocalDate;
import java.util.List;

public record TimesheetResponse(
        Long id,
        Long employeeId,
        LocalDate weekStart,
        LocalDate weekEnd,
        List<TimesheetEntryResponse> entries
) {
}
