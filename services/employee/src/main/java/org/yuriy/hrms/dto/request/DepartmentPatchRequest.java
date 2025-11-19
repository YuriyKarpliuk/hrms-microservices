package org.yuriy.hrms.dto.request;


public record DepartmentPatchRequest(String name,
        Long parentId,
        Long managerId) {
}
