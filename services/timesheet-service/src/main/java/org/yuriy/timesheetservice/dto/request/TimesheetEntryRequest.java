package org.yuriy.timesheetservice.dto.request;

import org.yuriy.timesheetservice.entity.ActivityType;

import java.time.LocalDate;

public record TimesheetEntryRequest(
        LocalDate workDate,
        ActivityType activityType,
        Double hours,
        String notes,
        String project
) {
}
