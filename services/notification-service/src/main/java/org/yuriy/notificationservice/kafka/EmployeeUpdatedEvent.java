package org.yuriy.notificationservice.kafka;

public record EmployeeUpdatedEvent(Long id, String email, String position, Long deptId, Long orgId) {
}
