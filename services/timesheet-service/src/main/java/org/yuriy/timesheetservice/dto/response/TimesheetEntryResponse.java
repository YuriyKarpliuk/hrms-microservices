package org.yuriy.timesheetservice.dto.response;

import java.time.LocalDate;

public record TimesheetEntryResponse(
        Long id,
        LocalDate workDate,
        String activityType,
        Double hours,
        String notes
) {
}
