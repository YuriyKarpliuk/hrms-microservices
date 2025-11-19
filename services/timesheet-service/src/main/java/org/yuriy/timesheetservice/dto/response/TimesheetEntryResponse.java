package org.yuriy.timesheetservice.dto.response;

import org.yuriy.timesheetservice.entity.ActivityType;

import java.time.LocalDate;

public record TimesheetEntryResponse(
        Long id,
        LocalDate workDate,
        String project,
        String notes,
        Double hours,
        ActivityType activityType
) {
}
