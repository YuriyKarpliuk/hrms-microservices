package org.yuriy.payrollservice.kafka;

import java.time.LocalDate;

public record PayrollFailedEvent(Long payrollId,
        Long employeeId,
        String employeeEmail,
        String status,
        LocalDate periodStart,
        LocalDate periodEnd
) {
}
