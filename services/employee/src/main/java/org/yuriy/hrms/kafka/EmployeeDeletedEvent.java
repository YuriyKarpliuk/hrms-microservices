package org.yuriy.hrms.kafka;

import org.yuriy.hrms.entity.Employee;

import java.time.LocalDate;

public record EmployeeDeletedEvent(Long id, Long orgId, Long deptId, LocalDate terminatedAt, Employee.Status status) {
}
