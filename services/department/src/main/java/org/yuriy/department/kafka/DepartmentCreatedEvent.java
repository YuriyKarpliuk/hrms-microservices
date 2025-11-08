package org.yuriy.department.kafka;

public record DepartmentCreatedEvent(
        Long id,
        Long orgId,
        String name,
        Long managerId
) {
}
