package org.yuriy.payrollservice.dto.response;

import org.yuriy.payrollservice.entity.PayrollStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PayrollResponse(
        Long id,
        Long employeeId,
        LocalDate periodStart,
        LocalDate periodEnd,
        BigDecimal baseSalary,
        BigDecimal bonus,
        BigDecimal deductions,
        BigDecimal netSalary,
        PayrollStatus status
) {
}
