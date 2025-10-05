package org.yuriy.department.kafka;

import java.time.LocalDate;

public record EmployeeDeletedEvent(Long id, Long orgId, Long deptId, LocalDate terminatedAt, String status) {
}

