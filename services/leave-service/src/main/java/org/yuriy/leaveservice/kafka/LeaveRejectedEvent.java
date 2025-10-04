package org.yuriy.leaveservice.kafka;

public record LeaveRejectedEvent(
        Long leaveId,
        Long employeeId,
        Long actionBy
) {}
