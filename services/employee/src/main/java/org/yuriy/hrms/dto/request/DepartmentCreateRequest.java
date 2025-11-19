package org.yuriy.hrms.dto.request;

public record DepartmentCreateRequest(
        Long orgId,
         String name,
        Long parentId,
        Long managerId
) {}
