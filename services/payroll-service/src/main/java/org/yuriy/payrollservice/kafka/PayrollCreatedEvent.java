package org.yuriy.payrollservice.kafka;

import java.time.LocalDate;

public record PayrollCreatedEvent(
        Long payrollId,
        Long employeeId,
        String employeeEmail,
        String status,
        LocalDate periodStart,
        LocalDate periodEnd
) {
}
