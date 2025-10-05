package org.yuriy.payrollservice.kafka;

import java.time.LocalDate;

public record LeaveRequestedEvent(Long leaveId, Long employeeId, LocalDate from, LocalDate to, String type,
        String reason) {
}
