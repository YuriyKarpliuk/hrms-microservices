package org.yuriy.payrollservice.kafka;

import java.time.LocalDate;

public record LeaveRejectedEvent(
        Long leaveId,
        Long employeeId,
        Long actionBy,
        LocalDate startDate,
        LocalDate endDate
) {}
