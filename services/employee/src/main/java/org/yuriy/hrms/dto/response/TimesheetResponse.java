package org.yuriy.hrms.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record TimesheetResponse(
        Long id,
        Long employeeId,
        String employeeFirstName,
        String employeeLastName,
        Double totalHours,
        String status,
        LocalDate weekStart,
        LocalDate weekEnd,
        LocalDateTime updatedAt
) {}
