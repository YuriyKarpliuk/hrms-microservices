package org.yuriy.department.kafka;

public record DepartmentCreatedEvent(
        Long id,
        Long orgId,
        String name,
        Long parentId,
        Long managerId
) {
}
