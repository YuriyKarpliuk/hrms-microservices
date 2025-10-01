package org.yuriy.department.dto.response;



public record DepartmentResponse(Long id,
        Long orgId,
        String name,
        Long parentId,
        Long managerId) {
}
