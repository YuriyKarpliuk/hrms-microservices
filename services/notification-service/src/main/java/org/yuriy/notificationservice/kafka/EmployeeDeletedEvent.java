package org.yuriy.notificationservice.kafka;

import java.time.LocalDate;

public record EmployeeDeletedEvent(Long id, Long orgId, Long deptId, LocalDate terminatedAt, String status) {
}
