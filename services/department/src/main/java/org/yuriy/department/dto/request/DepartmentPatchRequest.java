package org.yuriy.department.dto.request;


public record DepartmentPatchRequest(String name,
        Long parentId,
        Long managerId) {
}
