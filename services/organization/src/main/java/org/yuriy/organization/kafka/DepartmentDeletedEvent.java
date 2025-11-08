package org.yuriy.organization.kafka;

public record DepartmentDeletedEvent(
        Long id,
        Long orgId,
        String name,
        Long managerId
) {
}
