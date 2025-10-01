package org.yuriy.timesheetservice.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record TimesheetEntryRequest(
        @NotNull LocalDate workDate,
        @NotNull String activityType,
        @NotNull @Positive Double hours,
        String notes
) {
}
