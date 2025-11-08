package org.yuriy.department.kafka;

public record DepartmentDeletedEvent(
        Long id,
        Long orgId,
        String name,
        Long managerId
) {
}
