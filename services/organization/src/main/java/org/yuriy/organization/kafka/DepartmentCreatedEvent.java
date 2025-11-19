package org.yuriy.organization.kafka;

public record DepartmentCreatedEvent(
        Long id,
        Long orgId,
        String name,
        Long managerId
) {
}
