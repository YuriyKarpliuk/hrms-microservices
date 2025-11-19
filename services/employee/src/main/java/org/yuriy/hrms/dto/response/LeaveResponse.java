package org.yuriy.hrms.dto.response;

import java.time.LocalDate;

public record LeaveResponse(
        Long id,
        Long employeeId,
        String employeeFirstName,
        String employeeLastName,
        String type,
        String status,
        LocalDate startDate,
        LocalDate endDate
) {}
