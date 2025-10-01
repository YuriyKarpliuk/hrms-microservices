package org.yuriy.timesheetservice.dto.request;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;


public record TimesheetCreateRequest(
        @NotNull Long employeeId,
        @NotNull LocalDate weekStart,
        @NotNull LocalDate weekEnd,
        @NotEmpty List<TimesheetEntryRequest> entries
) {
}

