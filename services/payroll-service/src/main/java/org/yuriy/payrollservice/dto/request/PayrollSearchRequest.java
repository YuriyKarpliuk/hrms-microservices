package org.yuriy.payrollservice.dto.request;

import org.yuriy.payrollservice.entity.PayrollStatus;

import java.time.LocalDate;

public record PayrollSearchRequest(
        Long employeeId,
        LocalDate fromDate,
        LocalDate toDate,
        PayrollStatus status,
        Double minAmount,
        Double maxAmount
) {
}
