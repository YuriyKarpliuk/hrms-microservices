package org.yuriy.timesheetservice.dto.response;

import java.math.BigDecimal;

public record TimesheetSummaryResponse(
        BigDecimal hoursWorked,
        BigDecimal overtime,
        BigDecimal totalHours
) {}
