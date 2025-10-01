package org.yuriy.payrollservice.dto.request;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PayrollCreateRequest(
        @NotNull Long employeeId,
        @NotNull LocalDate periodStart,
        @NotNull LocalDate periodEnd,
        @NotNull BigDecimal baseSalary,
        BigDecimal bonus,
        BigDecimal deductions
) {
}
