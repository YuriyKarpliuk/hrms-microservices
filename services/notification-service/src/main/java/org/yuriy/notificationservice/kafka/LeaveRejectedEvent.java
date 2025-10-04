package org.yuriy.notificationservice.kafka;

public record LeaveRejectedEvent(
        Long leaveId,
        Long employeeId,
        Long actionBy
) {}
