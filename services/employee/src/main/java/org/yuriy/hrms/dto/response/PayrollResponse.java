package org.yuriy.hrms.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PayrollResponse(
        Long id,
        Long employeeId,
        String employeeFirstName,
        String employeeLastName,
        BigDecimal netSalary,
        String status,
        LocalDate periodStart,
        LocalDate periodEnd
) {}
