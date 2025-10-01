package org.yuriy.department.dto.request;


import org.yuriy.department.repository.specification.DepartmentSpecification;

public record DepartmentSearchRequest(String name, DepartmentSpecification.StringMatchType stringMatchType,
        Long managerId, Long orgId) {
}
