package org.yuriy.payrollservice.dto.response;

import org.yuriy.payrollservice.entity.PayrollStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PayrollWithEmployeeResponse(
        Long id,
        Long employeeId,
        String employeeFirstName,
        String employeeLastName,
        String employeeEmail,
        String employeePosition,
        LocalDate periodStart,
        LocalDate periodEnd,
        BigDecimal baseSalary,
        BigDecimal bonus,
        BigDecimal deductions,
        BigDecimal netSalary,
        PayrollStatus status
) {
}
