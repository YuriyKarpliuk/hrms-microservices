package org.yuriy.organization.kafka;

public record DepartmentUpdatedEvent(
        Long id,
        Long orgId,
        String name,
        Long parentId,
        Long managerId
) {
}
