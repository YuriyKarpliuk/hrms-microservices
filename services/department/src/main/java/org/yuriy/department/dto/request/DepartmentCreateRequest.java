package org.yuriy.department.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DepartmentCreateRequest(@NotNull Long orgId,
        @NotBlank String name,
        @NotNull Long parentId,
        @NotNull Long managerId) {
}

